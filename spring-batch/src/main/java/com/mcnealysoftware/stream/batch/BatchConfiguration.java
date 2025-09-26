package com.mcnealysoftware.stream.batch;

import model.Event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class BatchConfiguration {
    Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);

    @Bean
    public JdbcCursorItemReader<Event> reader(DataSource dataSource) {
        final var reader = new JdbcCursorItemReader<Event>() {
            public Long currentIndex = null;

            @Override
            public void open(ExecutionContext executionContext) throws ItemStreamException {
                if (executionContext.containsKey("CURRENT_INDEX")) {
                    logger.debug("got offset from context");
                    currentIndex = executionContext.getLong("CURRENT_INDEX");
                } else {
                    try (final var statement = dataSource.getConnection().prepareStatement("SELECT \"offset\" FROM batch_processed WHERE \"type\" = ?;")) {
                        statement.setString(1, "type");
                        final var result = statement.executeQuery();

                        if (result.next()) {
                            logger.debug("got offset from database");
                            currentIndex = result.getLong("offset");
                        } else {
                            logger.debug("no offset found");
                            currentIndex = 0L;
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                logger.debug("starting from offset {}", currentIndex);
                super.open(executionContext);
            }

            @Override
            public void update(ExecutionContext executionContext) throws ItemStreamException {
                executionContext.putLong("CURRENT_INDEX", currentIndex);
                super.update(executionContext);
            }
        };
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, type, message, timestamp FROM event WHERE processed = false AND id > ? ORDER BY id;");
        reader.setPreparedStatementSetter(ps -> ps.setLong(1, reader.currentIndex));
        reader.setRowMapper((resultSet, rowNumber) -> {
            reader.currentIndex = resultSet.getLong("id");
            return new Event(
                    resultSet.getInt("id"),
                    resultSet.getString("type"),
                    resultSet.getString("message"),
                    resultSet.getTimestamp("timestamp"));
        });
        return reader;
    }

    @Bean
    public ItemProcessor<Event, Event> processor() {
        return (a -> {
            logger.info("Processing event {} : {}", a.id(), a.message());
            /*
              Do stuff here, like aggregate, send emails.
              Though side effects outside the database transaction
              may happen more than once.
             */
            return a;
        });
    }

    @Bean
    public JdbcBatchItemWriter<Event> writer(DataSource dataSource) {
        final var writer = new JdbcBatchItemWriter<Event>();
        writer.setDataSource(dataSource);
        writer.setSql("UPDATE event SET processed = true WHERE id = ?;");
        writer.setItemPreparedStatementSetter((item, pss) -> {
            logger.debug("Writing processed = true for event {}", item.id());
            pss.setInt(1, item.id());
        });
        return writer;
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        final ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"CURRENT_INDEX"});
        return listener;
    }

    @Bean
    public JobExecutionListener completionListener(DataSource datasource) {
        return new JobExecutionListener() {
            @Override
            public void afterJob(@NonNull JobExecution jobExecution) {
                if (jobExecution.getExecutionContext().containsKey("CURRENT_INDEX")) {
                    long offset = jobExecution.getExecutionContext().getLong("CURRENT_INDEX");
                    logger.debug("saving offset to database {}", offset);
                    try (final var statement = datasource.getConnection().prepareStatement("INSERT INTO batch_processed (\"type\", \"offset\") VALUES (?, ?) ON CONFLICT (\"type\") DO UPDATE SET \"offset\" = EXCLUDED.offset")) {
                        statement.setString(1, "type");
                        statement.setLong(2, offset);
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                logger.info("done");
            }
        };
    }

    @Bean
    public Job importUserJob(Step step1, JobRepository jobRepository, JobExecutionListener listener) {
        return new JobBuilder("importEventJob", jobRepository)
                .listener(listener)
                .start(step1)
                .build();
    }

    @Bean
    public Step step1(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                      JdbcCursorItemReader<Event> reader, ItemProcessor<Event, Event> processor, JdbcBatchItemWriter<Event> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Event, Event>chunk(3, transactionManager)
                .reader(reader)
                .listener(promotionListener())
                .processor(processor)
                .writer(writer)
                .allowStartIfComplete(true)
                .build();
    }
}

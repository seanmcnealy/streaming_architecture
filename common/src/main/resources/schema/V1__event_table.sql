CREATE TABLE IF NOT EXISTS event (
    id SERIAL PRIMARY KEY,
    type TEXT NOT NULL,
    message TEXT DEFAULT NULL,
    processed BOOLEAN DEFAULT false,
    timestamp TIMESTAMP DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS batch_processed (
    type TEXT NOT NULL,
    "offset" INTEGER NOT NULL,

    CONSTRAINT batch_processed_type UNIQUE (type)
 );

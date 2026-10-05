ALTER TABLE materials
ADD COLUMN s3_key VARCHAR(1024);

ALTER TABLE materials
RENAME COLUMN kind TO file_type;

ALTER TABLE materials
RENAME COLUMN size TO file_size;

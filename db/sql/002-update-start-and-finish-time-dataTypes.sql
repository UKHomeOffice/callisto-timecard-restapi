ALTER TABLE timecard.time_entry
ALTER COLUMN actual_start_time TYPE TIMESTAMP WITH TIME ZONE USING actual_start_time::TIMESTAMP WITH TIME ZONE,
ALTER COLUMN actual_end_time TYPE TIMESTAMP WITH TIME ZONE USING actual_end_time::TIMESTAMP WITH TIME ZONE;
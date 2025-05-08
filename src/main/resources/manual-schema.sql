CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER DATABASE "remedy-db"
SET
  TIMEZONE TO 'Asia/Kuwait';

-- Enum for bag state
CREATE TYPE bag_state AS ENUM(
  'unsealed',
  'sealed',
  'loaded',
  'dispensed',
  'discarded'
);

-- Enum for medication history action
CREATE TYPE history_action AS ENUM('dispensed', 'discarded');

-- Prescribers
CREATE TABLE prescribers (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
                             name VARCHAR(100) NOT NULL,
                             email VARCHAR(255) UNIQUE NOT NULL,
                             password VARCHAR(255) NOT NULL

);

-- Patients
CREATE TABLE patients (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
                          prescriber_id UUID REFERENCES prescribers (id),
                          name VARCHAR(100) NOT NULL,
                          dob DATE NOT NULL,
                          email VARCHAR(255) UNIQUE NOT NULL,
                          phone VARCHAR(20),
                          password VARCHAR(255) NOT NULL,
                          face_image_path TEXT);

-- Bags
CREATE TABLE bags (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
                      patient_id UUID REFERENCES patients (id),
                      prescription TEXT NOT NULL,
                      state bag_state NOT NULL,
                      created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Medication History
CREATE TABLE medication_history (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
                                    bag_id UUID REFERENCES bags (id) ON DELETE CASCADE,
                                    patient_id UUID REFERENCES patients (id) ON DELETE CASCADE,
                                    prescriber_id UUID REFERENCES prescribers (id) ON DELETE SET NULL,
                                    action history_action NOT NULL,
                                    prescription_copy TEXT NOT NULL,
                                    TIMESTAMP TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Add role to prescribers
ALTER TABLE prescribers
    ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'PRESCRIBER';

-- Add role to patients
ALTER TABLE patients
    ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'PATIENT';


-- First, modify bags table to change the state column to VARCHAR
ALTER TABLE bags
    ALTER COLUMN state TYPE VARCHAR(20);

-- Then drop the enum type (optional, only if you want to completely remove it)
DROP TYPE bag_state;



-- Modify the medication_history table to use VARCHAR for action
ALTER TABLE medication_history
    ALTER COLUMN action TYPE VARCHAR(20);
-- drop the existing enum type
DROP TYPE history_action;


ALTER TABLE bags
    DROP CONSTRAINT bags_patient_id_fkey,
    ADD CONSTRAINT bags_patient_id_fkey
        FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE;

alter table prescribers
    add phone text;
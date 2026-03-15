-- Auto-runs when PostgreSQL container starts for the first time

CREATE TABLE IF NOT EXISTS company_entity (
    id SERIAL PRIMARY KEY,
    company_name VARCHAR(255) UNIQUE,
    started_at DATE,
    is_registered BOOLEAN
);

INSERT INTO company_entity (company_name, started_at, is_registered)
VALUES
    ('Tech Corp',     '2020-01-15', true),
    ('StartupXYZ',    '2022-06-01', false),
    ('Innovate Ltd',  '2019-03-20', true),
    ('DevSolutions',  '2021-09-10', true),
    ('CloudBase Inc', '2023-03-05', false)
ON CONFLICT (company_name) DO NOTHING;

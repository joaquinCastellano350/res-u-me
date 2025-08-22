CREATE TABLE resumes (
                         id  UUID PRIMARY KEY,
                         USER_ID     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                         s3_key  TEXT NOT NULL,
                         file_name TEXT NOT NULL,
                         mime_type TEXT NOT NULL,
                         file_size BIGINT NOT NULL,
                         uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_resumes_user ON resumes(user_id);
ALTER TABLE resumes
    ADD COLUMN visibility TEXT NOT NULL DEFAULT 'PRIVATE',
    ADD COLUMN published_at TIMESTAMPTZ,
    ADD COLUMN share_token_hash TEXT,
    ADD COLUMN share_expirates_at TIMESTAMPTZ,
    ADD COLUMN share_created_at TIMESTAMPTZ;


ALTER TABLE resumes
    ADD CONSTRAINT resumes_visibility_chk
    CHECK (visibility IN ('PRIVATE', 'PUBLIC', 'UNLISTED'));

CREATE INDEX idx_resumes_public ON resumes(visibility, uploaded_at DESC);

CREATE TABLE tags (
    id UUID PRIMARY KEY,
    name CITEXT NOT NULL UNIQUE
);

CREATE TABLE resume_tags (
    resume_id UUID NOT NULL REFERENCES resumes(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (resume_id, tag_id)
);

CREATE INDEX idx_resume_tags_tag ON resume_tags(tag_id, resume_id);
CREATE INDEX idx_resume_tags_resume ON resume_tags(resume_id,tag_id);
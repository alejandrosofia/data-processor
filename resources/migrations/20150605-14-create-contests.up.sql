CREATE TABLE contests (id INTEGER NOT NULL,
                       results_id INTEGER REFERENCES results (id) NOT NULL,
                       PRIMARY KEY (results_id, id),
                       election_id INTEGER,
                       electoral_district_id INTEGER,
                       type TEXT,
                       partisan BOOLEAN,
                       primary_party TEXT,
                       electorate_specifications TEXT,
                       special BOOLEAN,
                       office TEXT,
                       filing_closed_date DATE,
                       number_elected INTEGER,
                       number_voting_for INTEGER,
                       ballot_id INTEGER,
                       ballot_placement INTEGER);
CREATE INDEX IF NOT EXISTS idx_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_card_number ON payment_cards(number);
CREATE INDEX IF NOT EXISTS idx_user_id_of_card ON payment_cards(user_id);
CREATE INDEX IF NOT EXISTS idx_user_name ON users(firstname,surname);
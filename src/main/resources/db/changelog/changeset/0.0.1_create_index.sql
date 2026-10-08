CREATE INDEX IF NOT EXISTS idx_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_card_number ON payment_cards(number);
CREATE INDEX IF NOT EXISTS idx_card_id_active ON payment_cards(id,number);
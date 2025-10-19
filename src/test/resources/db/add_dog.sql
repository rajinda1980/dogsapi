INSERT INTO dogs (id, name, breed, supplier_id, badge_id, birth_date, date_acquired, gender, current_status, leaving_reason,
leaving_date, kennelling_characteristic, deleted, created_at)
VALUES (1, 'Rex', 'German Shepherd', 1, 'K9-001', '2021-05-20', '2022-02-15', 'MALE', 'IN_SERVICE', 'TRANSFERRED', NULL,
'Strong, obedient, and alert. Excellent tracking ability.', FALSE, NOW());
COMMIT;

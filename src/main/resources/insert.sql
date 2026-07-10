-- =====================================================================
-- SCRIPT COMBINADO DE DATOS DE PRUEBA (PostgreSQL)
-- Parte 1: Clínicas de prueba (para probar el registro de doctores)
-- Parte 2: Datos de la Clínica San Juan (médicos, citas, reseñas)
-- Parte 3: Clínicas y médicos 100% nuevos (dominio @newclinics.test)
--
-- Contraseña para las cuentas nuevas: Test1234!
-- (mismo hash BCrypt real usado en el script de Armando Castillo)
-- =====================================================================

-- ═══════════════════════════════════════════════════════════════════
-- PARTE 1: Clínicas de prueba para poder probar el registro de doctores
-- ═══════════════════════════════════════════════════════════════════

-- Limpieza (permite re-ejecutar el script sin duplicar)
DELETE FROM clinics WHERE name IN ('Clínica San Isidro', 'Clínica Miraflores', 'Consultorio Surco Salud');

INSERT INTO clinics (name, description, phone, email, street, district, city, country, is_active, rating, review_count, emergencia_24h, estacionamiento, farmacia, laboratorio, imagenologia, servicio_ambulancia, unidad_cuidados_intensivos, hospitalizacion) VALUES
                                                                                                                                                                                                                                                                    ('Clínica San Isidro', 'Centro médico especializado en consulta ambulatoria', '014567890', 'contacto@clinicasanisidro.pe', 'Av. Javier Prado 123', 'San Isidro', 'Lima', 'Perú', true, 4.50, 120, true, true, true, true, true, true, false, true),
                                                                                                                                                                                                                                                                    ('Clínica Miraflores', 'Atención médica general y especialidades', '014567891', 'contacto@clinicamiraflores.pe', 'Av. Larco 456', 'Miraflores', 'Lima', 'Perú', true, 4.20, 85, false, true, true, false, false, false, false, false),
                                                                                                                                                                                                                                                                    ('Consultorio Surco Salud', 'Consultorio privado multiespecialidad', '014567892', 'contacto@surcosalud.pe', 'Av. Caminos del Inca 789', 'Surco', 'Lima', 'Perú', true, 4.00, 40, false, false, false, false, false, false, false, false);

-- ═══════════════════════════════════════════════════════════════════
-- PARTE 2: DATOS DE PRUEBA PARA EL DASHBOARD DE CLÍNICA SAN JUAN
-- Crea médicos, departamentos, citas y reseñas asociados a la clínica
-- clinicasanjuan@gmail.com para que el dashboard muestre datos reales.
-- ═══════════════════════════════════════════════════════════════════

-- ── Limpieza (permite re-ejecutar el script sin duplicar) ────────────
DELETE FROM provider_reviews          WHERE id BETWEEN 9101 AND 9110;
DELETE FROM appointments_appointments WHERE id BETWEEN 9101 AND 9120;
-- FIX: provider_services referencia a users_providers vía FK; hay que
-- borrarlo antes o el DELETE de users_providers falla con error 23503.
DELETE FROM provider_services         WHERE provider_id BETWEEN 9101 AND 9103;
DELETE FROM provider_schedule         WHERE provider_id BETWEEN 9101 AND 9103;
DELETE FROM provider_clinics          WHERE provider_id BETWEEN 9101 AND 9103;
DELETE FROM departments               WHERE clinic_id IN (SELECT id FROM clinics WHERE email = 'clinicasanjuan@gmail.com');
DELETE FROM users_providers           WHERE id BETWEEN 9101 AND 9103;
DELETE FROM users_patients            WHERE id BETWEEN 9101 AND 9103;
DELETE FROM auth_email_verification_tokens WHERE user_id BETWEEN 9101 AND 9106;
DELETE FROM auth_password_reset_tokens     WHERE user_id BETWEEN 9101 AND 9106;
DELETE FROM security_activity              WHERE auth_user_id BETWEEN 9101 AND 9106;
DELETE FROM auth_users                WHERE id BETWEEN 9101 AND 9106;

-- ── Usuarios (auth_users) ─────────────────────────────────────────────
-- role_id: 1 = Patient, 2 = Provider
INSERT INTO auth_users (id, email, password, role_id, is_verified, created_at, failed_login_attempts)
VALUES
    (9101, 'carlos.mendoza.sanjuan@example.com',  '$2b$10$w4mpKqAUDetIcmNa5fQvaurvwyW/.anq0Re/DlpuTKK2DcKWOnMo6', 2, true, NOW(), 0),
    (9102, 'lucia.fernandez.sanjuan@example.com', '$2b$10$w4mpKqAUDetIcmNa5fQvaurvwyW/.anq0Re/DlpuTKK2DcKWOnMo6', 2, true, NOW(), 0),
    (9103, 'miguel.torres.sanjuan@example.com',   '$2b$10$w4mpKqAUDetIcmNa5fQvaurvwyW/.anq0Re/DlpuTKK2DcKWOnMo6', 2, true, NOW(), 0),
    (9104, 'ana.rivas.sanjuan@example.com',       '$2b$10$w4mpKqAUDetIcmNa5fQvaurvwyW/.anq0Re/DlpuTKK2DcKWOnMo6', 1, true, NOW(), 0),
    (9105, 'pedro.solis.sanjuan@example.com',     '$2b$10$w4mpKqAUDetIcmNa5fQvaurvwyW/.anq0Re/DlpuTKK2DcKWOnMo6', 1, true, NOW(), 0),
    (9106, 'karla.vega.sanjuan@example.com',      '$2b$10$w4mpKqAUDetIcmNa5fQvaurvwyW/.anq0Re/DlpuTKK2DcKWOnMo6', 1, true, NOW(), 0);

-- ── Pacientes (users_patients) ────────────────────────────────────────
INSERT INTO users_patients (id, user_id, name, phone, birthday, country, dni, genero)
VALUES
    (9101, 9104, 'Ana Rivas',   '987121212', '1992-03-15', 'Perú', '74111111', 'Femenino'),
    (9102, 9105, 'Pedro Solís', '987343434', '1988-07-22', 'Perú', '74222222', 'Masculino'),
    (9103, 9106, 'Karla Vega',  '987565656', '1995-11-05', 'Perú', '74333333', 'Femenino');

-- ── Proveedores (users_providers) ───────────────────────────────────────
INSERT INTO users_providers (
    id, user_id, full_name, phone, specialty, description,
    rating, is_validated, price_per_appointment, average_rating,
    experience_years, language, modality, duration_minutes,
    street, district, city, country, cedula_profesional, registro_medico
) VALUES
      (9101, 9101, 'Carlos Mendoza', '987111333', 'Cardiología',
       'Cardiólogo con enfoque en prevención y salud cardiovascular integral.',
       4.7, true, 180.00, 4.7, 12, 'Español', 'Presencial', 30,
       'Av. Los Álamos 245', 'San Isidro', 'Lima', 'Perú', 'CMP 44444', 'RNE 1111'),
      (9102, 9102, 'Lucía Fernández', '987222444', 'Pediatría',
       'Pediatra especializada en atención integral infantil y adolescente.',
       4.9, true, 150.00, 4.9, 8, 'Español', 'Presencial', 30,
       'Av. Los Álamos 245', 'San Isidro', 'Lima', 'Perú', 'CMP 55556', 'RNE 2222'),
      (9103, 9103, 'Miguel Torres', '987333555', 'Traumatología',
       'Traumatólogo con experiencia en cirugía ortopédica y rehabilitación.',
       4.6, true, 200.00, 4.6, 15, 'Español', 'Presencial', 40,
       'Av. Los Álamos 245', 'San Isidro', 'Lima', 'Perú', 'CMP 66667', 'RNE 3333');

-- ── Vínculo proveedor-clínica (provider_clinics) ────────────────────────
INSERT INTO provider_clinics (provider_id, clinic_id)
SELECT p.id, c.id
FROM users_providers p, clinics c
WHERE p.id IN (9101, 9102, 9103)
  AND c.email = 'clinicasanjuan@gmail.com';

-- ── Horario de los proveedores (provider_schedule) ──────────────────────
INSERT INTO provider_schedule (provider_id, horario_item) VALUES
                                                              (9101, 'Lunes, Miércoles, Viernes: 08:00 - 14:00'),
                                                              (9102, 'Lunes, Martes, Jueves, Viernes: 09:00 - 17:00'),
                                                              (9103, 'Martes, Jueves, Sábado: 10:00 - 16:00');

-- ── Departamentos de la clínica (departments) ───────────────────────────
INSERT INTO departments (clinic_id, name, capacity, current_patients)
SELECT id, 'Cardiología', 15, 6 FROM clinics WHERE email = 'clinicasanjuan@gmail.com'
UNION ALL
SELECT id, 'Pediatría', 20, 12 FROM clinics WHERE email = 'clinicasanjuan@gmail.com'
UNION ALL
SELECT id, 'Traumatología', 10, 4 FROM clinics WHERE email = 'clinicasanjuan@gmail.com';

-- ── Citas de HOY ────────────────────────────────────────────────────────
INSERT INTO appointments_appointments
(id, patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, amount_paid, created_at)
VALUES
    (9101, 9101, 9101, 'Consulta cardiológica', CURRENT_DATE, '09:00:00', '09:30:00', 'CONFIRMED', NULL, NOW()),
    (9102, 9102, 9102, 'Control pediátrico',    CURRENT_DATE, '11:00:00', '11:30:00', 'SCHEDULED', NULL, NOW()),
    (9103, 9103, 9103, 'Consulta traumatológica', CURRENT_DATE, '15:00:00', '15:40:00', 'CONFIRMED', NULL, NOW());

-- ── Citas de este mes (además de las de hoy) ───────────────────────────
INSERT INTO appointments_appointments
(id, patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, amount_paid, created_at)
VALUES
    (9104, 9101, 9102, 'Control pediátrico',      CURRENT_DATE - INTERVAL '2 days', '10:00:00', '10:30:00', 'COMPLETED', 150.00, NOW() - INTERVAL '2 days'),
    (9105, 9102, 9101, 'Consulta cardiológica',   CURRENT_DATE - INTERVAL '4 days', '09:00:00', '09:30:00', 'COMPLETED', 180.00, NOW() - INTERVAL '4 days'),
    (9106, 9103, 9103, 'Consulta traumatológica', CURRENT_DATE - INTERVAL '3 days', '16:00:00', '16:40:00', 'CANCELLED', NULL,   NOW() - INTERVAL '3 days'),
    (9107, 9101, 9103, 'Consulta traumatológica', CURRENT_DATE - INTERVAL '6 days', '11:00:00', '11:40:00', 'COMPLETED', 200.00, NOW() - INTERVAL '6 days'),
    (9108, 9102, 9102, 'Control pediátrico',      CURRENT_DATE - INTERVAL '8 days', '09:30:00', '10:00:00', 'COMPLETED', 150.00, NOW() - INTERVAL '8 days'),
    (9109, 9103, 9101, 'Consulta cardiológica',   CURRENT_DATE - INTERVAL '10 days', '14:00:00', '14:30:00', 'COMPLETED', 180.00, NOW() - INTERVAL '10 days');

-- ── Citas del mes anterior (para comparar deltas %) ────────────────────
INSERT INTO appointments_appointments
(id, patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, amount_paid, created_at)
VALUES
    (9110, 9101, 9101, 'Consulta cardiológica',   CURRENT_DATE - INTERVAL '1 month', '10:00:00', '10:30:00', 'COMPLETED', 180.00, NOW() - INTERVAL '1 month'),
    (9111, 9102, 9102, 'Control pediátrico',      CURRENT_DATE - INTERVAL '1 month', '11:00:00', '11:30:00', 'COMPLETED', 150.00, NOW() - INTERVAL '1 month'),
    (9112, 9103, 9103, 'Consulta traumatológica', CURRENT_DATE - INTERVAL '1 month', '12:00:00', '12:40:00', 'CANCELLED', NULL,   NOW() - INTERVAL '1 month'),
    (9113, 9101, 9102, 'Control pediátrico',      CURRENT_DATE - INTERVAL '1 month', '13:00:00', '13:30:00', 'COMPLETED', 150.00, NOW() - INTERVAL '1 month');

-- ── Reseñas (provider_reviews) ─────────────────────────────────────────
INSERT INTO provider_reviews (id, provider_id, patient_id, appointment_id, rating, comment, created_at)
VALUES
    (9101, 9101, 9101, 9105, 5, 'Excelente atención, el doctor explicó todo con mucha claridad.', NOW() - INTERVAL '3 hours'),
    (9102, 9102, 9102, 9108, 5, 'La doctora fue muy paciente con mi hijo, se nota su experiencia.', NOW() - INTERVAL '1 day'),
    (9103, 9103, 9101, 9107, 4, 'Buen trato, aunque la espera fue un poco larga.', NOW() - INTERVAL '4 days');

-- ═══════════════════════════════════════════════════════════════════
-- PARTE 3: DATOS DE PRUEBA — CLÍNICAS Y MÉDICOS 100% NUEVOS
-- 1 paciente + 5 clínicas (tabla `clinics`, role_id=4) + 1 médico real
-- por clínica (tabla `users_providers`, role_id=2, vinculado vía
-- `provider_clinics`) + favoritos + historial de búsquedas + citas.
-- Nombres y emails inventados, con dominio propio (@newclinics.test),
-- para no chocar con ninguna clínica/médico que ya exista en tu BD.
-- Contraseña para todas las cuentas: Password123
-- ═══════════════════════════════════════════════════════════════════

BEGIN;

-- ---------------------------------------------------------------------
-- 0) Limpieza de datos de prueba previos (en orden por dependencias FK)
--    Como los nombres son nuevos y exclusivos de este script, es
--    seguro hacer DELETE directo (no hay datos reales que puedan
--    colgar de estas filas).
-- ---------------------------------------------------------------------
DELETE FROM search_history WHERE user_id IN (
    SELECT id FROM auth_users WHERE email LIKE '%@newclinics.test'
);
DELETE FROM favorites WHERE patient_id IN (
    SELECT up.id FROM users_patients up
                          JOIN auth_users au ON au.id = up.user_id
    WHERE au.email LIKE '%@newclinics.test'
);

-- FIX: hay que borrar las reviews ANTES que los appointments, porque
-- provider_reviews.appointment_id referencia a appointments_appointments.
-- Sin este DELETE, la segunda corrida del script falla con un error de
-- llave foránea (23503) al intentar borrar appointments que aún tienen
-- reviews colgando.
DELETE FROM provider_reviews WHERE provider_id IN (
    SELECT pr.id FROM users_providers pr
                          JOIN auth_users au ON au.id = pr.user_id
    WHERE au.email LIKE '%@newclinics.test'
);

DELETE FROM appointments_appointments WHERE patient_id IN (
    SELECT up.id FROM users_patients up
                          JOIN auth_users au ON au.id = up.user_id
    WHERE au.email LIKE '%@newclinics.test'
);
DELETE FROM provider_clinics WHERE provider_id IN (
    SELECT pr.id FROM users_providers pr
                          JOIN auth_users au ON au.id = pr.user_id
    WHERE au.email LIKE '%@newclinics.test'
);

-- FIX: provider_services también referencia a users_providers vía FK
-- (fkn7tgu1b6i4n6xl5g1i38b6w16). Hay que borrarlo antes del DELETE de
-- users_providers o falla con error 23503 en la segunda corrida.
DELETE FROM provider_services WHERE provider_id IN (
    SELECT pr.id FROM users_providers pr
                          JOIN auth_users au ON au.id = pr.user_id
    WHERE au.email LIKE '%@newclinics.test'
);

DELETE FROM users_providers WHERE user_id IN (
    SELECT id FROM auth_users WHERE email LIKE '%@newclinics.test'
);
DELETE FROM clinics WHERE user_id IN (
    SELECT id FROM auth_users WHERE email LIKE '%@newclinics.test'
);
DELETE FROM users_patients WHERE user_id IN (
    SELECT id FROM auth_users WHERE email LIKE '%@newclinics.test'
);
DELETE FROM security_activity WHERE auth_user_id IN (
    SELECT id FROM auth_users WHERE email LIKE '%@newclinics.test'
);
DELETE FROM auth_email_verification_tokens WHERE user_id IN (
    SELECT id FROM auth_users WHERE email LIKE '%@newclinics.test'
);
DELETE FROM auth_password_reset_tokens WHERE user_id IN (
    SELECT id FROM auth_users WHERE email LIKE '%@newclinics.test'
);
DELETE FROM auth_users WHERE email LIKE '%@newclinics.test';

DELETE FROM offers WHERE service_id IN (
    SELECT id FROM catalog_services WHERE category_id IN (
        SELECT id FROM catalog_service_categories WHERE name IN (
                                                                 'Medicina general', 'Odontología', 'Ginecología', 'Cardiología'
            )
    )
);
DELETE FROM catalog_services WHERE category_id IN (
    SELECT id FROM catalog_service_categories WHERE name IN (
                                                             'Medicina general', 'Odontología', 'Ginecología', 'Cardiología'
        )
);
DELETE FROM catalog_service_categories WHERE name IN (
                                                      'Medicina general', 'Odontología', 'Ginecología', 'Cardiología'
    );
DELETE FROM catalog_specialties WHERE name IN (
                                               'Cardiología', 'Pediatría', 'Dermatología', 'Oftalmología', 'Traumatología', 'Ginecología'
    );

-- ---------------------------------------------------------------------
-- 1) Catálogos: especialidades, categorías de servicio, servicios
-- ---------------------------------------------------------------------
INSERT INTO catalog_specialties (name, description, is_active) VALUES
                                                                   ('Cardiología',   'Salud del corazón',         true),
                                                                   ('Pediatría',     'Atención de niños y bebés', true),
                                                                   ('Dermatología',  'Salud de la piel',          true),
                                                                   ('Oftalmología',  'Salud visual',              true),
                                                                   ('Traumatología', 'Lesiones y huesos',         true),
                                                                   ('Ginecología',   'Salud femenina',            true);

INSERT INTO catalog_service_categories (name, description, is_active) VALUES
                                                                          ('Medicina general', 'Consultas generales',          true),
                                                                          ('Odontología',      'Cuidado dental',                true),
                                                                          ('Ginecología',       'Salud femenina y prenatal',     true),
                                                                          ('Cardiología',       'Evaluación y cuidado cardíaco', true);

INSERT INTO catalog_services (name, description, price, is_active, category_id)
SELECT 'Consulta general', 'Evaluación médica general', 80.00, true, id
FROM catalog_service_categories WHERE name = 'Medicina general';

INSERT INTO catalog_services (name, description, price, is_active, category_id)
SELECT 'Limpieza dental', 'Limpieza y revisión dental', 120.00, true, id
FROM catalog_service_categories WHERE name = 'Odontología';

INSERT INTO catalog_services (name, description, price, is_active, category_id)
SELECT 'Ecografía 4D', 'Ecografía prenatal en alta definición', 250.00, true, id
FROM catalog_service_categories WHERE name = 'Ginecología';

INSERT INTO catalog_services (name, description, price, is_active, category_id)
SELECT 'Consulta cardiológica', 'Evaluación cardiológica completa', 180.00, true, id
FROM catalog_service_categories WHERE name = 'Cardiología';

-- ---------------------------------------------------------------------
-- 2) Usuarios (auth_users)
--    role_id: 1 = Patient, 2 = Provider, 3 = Admin, 4 = Clinic
--    password (BCrypt) para todos: Password123
-- ---------------------------------------------------------------------
INSERT INTO auth_users (email, password, role_id, is_verified, created_at, failed_login_attempts)
VALUES
    -- Paciente
    ('valeria.chumpitaz@newclinics.test', '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 1, true, NOW(), 0),

    -- Clínicas (role_id = 4)
    ('clinica.vitalis@newclinics.test',    '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 4, true, NOW(), 0),
    ('clinica.aurora@newclinics.test',     '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 4, true, NOW(), 0),
    ('clinica.novavision@newclinics.test', '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 4, true, NOW(), 0),
    ('clinica.andes@newclinics.test',      '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 4, true, NOW(), 0),
    ('clinica.bienestar@newclinics.test',  '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 4, true, NOW(), 0),

    -- Médicos, 1 por clínica (role_id = 2)
    ('dr.vitalis@newclinics.test',    '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 2, true, NOW(), 0),
    ('dr.aurora@newclinics.test',     '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 2, true, NOW(), 0),
    ('dr.novavision@newclinics.test', '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 2, true, NOW(), 0),
    ('dr.andes@newclinics.test',      '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 2, true, NOW(), 0),
    ('dr.bienestar@newclinics.test',  '$2b$12$cZncXBYB0R/X6dBb/fpTnOV.lUjiTP3hxe2V7pTGITZd.KSnmxdK6', 2, true, NOW(), 0);

-- ---------------------------------------------------------------------
-- 3) Paciente (vinculado al primer auth_user)
-- ---------------------------------------------------------------------
INSERT INTO users_patients (user_id, name, phone, birthday, country)
SELECT id, 'Valeria Chumpitaz', '+51 988112233', DATE '1993-08-02', 'Perú'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

-- ---------------------------------------------------------------------
-- 4) Clínicas (tabla `clinics`, vinculadas 1-a-1 a su auth_user)
-- ---------------------------------------------------------------------
INSERT INTO clinics (user_id, name, email, phone, street, district, city, country, is_active, rating, review_count)
SELECT id, 'Clínica Vitalis', email, '01 400-1001',
       'Av. Alfredo Benavides 2140', 'Miraflores', 'Lima', 'Perú', true, 4.6, 95
FROM auth_users WHERE email = 'clinica.vitalis@newclinics.test';

INSERT INTO clinics (user_id, name, email, phone, street, district, city, country, is_active, rating, review_count)
SELECT id, 'Centro Cardiológico Aurora', email, '01 400-1002',
       'Av. Angamos Este 1450', 'Surquillo', 'Lima', 'Perú', true, 4.8, 130
FROM auth_users WHERE email = 'clinica.aurora@newclinics.test';

INSERT INTO clinics (user_id, name, email, phone, street, district, city, country, is_active, rating, review_count)
SELECT id, 'Clínica Nova Visión', email, '01 400-1003',
       'Av. Petit Thouars 3899', 'San Isidro', 'Lima', 'Perú', true, 4.7, 88
FROM auth_users WHERE email = 'clinica.novavision@newclinics.test';

INSERT INTO clinics (user_id, name, email, phone, street, district, city, country, is_active, rating, review_count)
SELECT id, 'Policlínico Andes Salud', email, '01 400-1004',
       'Av. Aviación 2680', 'San Borja', 'Lima', 'Perú', true, 4.5, 102
FROM auth_users WHERE email = 'clinica.andes@newclinics.test';

INSERT INTO clinics (user_id, name, email, phone, street, district, city, country, is_active, rating, review_count)
SELECT id, 'Clínica Bienestar Total', email, '01 400-1005',
       'Av. La Molina 1785', 'La Molina', 'Lima', 'Perú', true, 4.6, 76
FROM auth_users WHERE email = 'clinica.bienestar@newclinics.test';

-- ---------------------------------------------------------------------
-- 5) Médicos (tabla `users_providers`) — 1 por clínica
-- ---------------------------------------------------------------------
INSERT INTO users_providers
(user_id, full_name, phone, specialty, description, rating, is_validated,
 price_per_appointment, average_rating, experience_years, street, district, city, country)
SELECT id, 'Renato Aguilar', '987200001', 'Medicina general',
       'Médico general en Clínica Vitalis.', 4.6, true,
       130.00, 4.6, 10, 'Av. Alfredo Benavides 2140', 'Miraflores', 'Lima', 'Perú'
FROM auth_users WHERE email = 'dr.vitalis@newclinics.test';

INSERT INTO users_providers
(user_id, full_name, phone, specialty, description, rating, is_validated,
 price_per_appointment, average_rating, experience_years, street, district, city, country)
SELECT id, 'Melissa Castro', '987200002', 'Cardiología',
       'Cardióloga en Centro Cardiológico Aurora.', 4.8, true,
       170.00, 4.8, 15, 'Av. Angamos Este 1450', 'Surquillo', 'Lima', 'Perú'
FROM auth_users WHERE email = 'dr.aurora@newclinics.test';

INSERT INTO users_providers
(user_id, full_name, phone, specialty, description, rating, is_validated,
 price_per_appointment, average_rating, experience_years, street, district, city, country)
SELECT id, 'Iván Bustamante', '987200003', 'Oftalmología',
       'Oftalmólogo en Clínica Nova Visión.', 4.7, true,
       160.00, 4.7, 11, 'Av. Petit Thouars 3899', 'San Isidro', 'Lima', 'Perú'
FROM auth_users WHERE email = 'dr.novavision@newclinics.test';

INSERT INTO users_providers
(user_id, full_name, phone, specialty, description, rating, is_validated,
 price_per_appointment, average_rating, experience_years, street, district, city, country)
SELECT id, 'Fiorella Núñez', '987200004', 'Ginecología',
       'Ginecóloga en Policlínico Andes Salud.', 4.5, true,
       175.00, 4.5, 13, 'Av. Aviación 2680', 'San Borja', 'Lima', 'Perú'
FROM auth_users WHERE email = 'dr.andes@newclinics.test';

INSERT INTO users_providers
(user_id, full_name, phone, specialty, description, rating, is_validated,
 price_per_appointment, average_rating, experience_years, street, district, city, country)
SELECT id, 'Sebastián Prado', '987200005', 'Medicina general',
       'Médico general en Clínica Bienestar Total.', 4.6, true,
       140.00, 4.6, 9, 'Av. La Molina 1785', 'La Molina', 'Lima', 'Perú'
FROM auth_users WHERE email = 'dr.bienestar@newclinics.test';

-- ---------------------------------------------------------------------
-- 6) Vínculo médico-clínica (provider_clinics)
-- ---------------------------------------------------------------------
INSERT INTO provider_clinics (provider_id, clinic_id)
SELECT pr.id, c.id
FROM (VALUES
          ('dr.vitalis@newclinics.test',    'clinica.vitalis@newclinics.test'),
          ('dr.aurora@newclinics.test',     'clinica.aurora@newclinics.test'),
          ('dr.novavision@newclinics.test', 'clinica.novavision@newclinics.test'),
          ('dr.andes@newclinics.test',      'clinica.andes@newclinics.test'),
          ('dr.bienestar@newclinics.test',  'clinica.bienestar@newclinics.test')
     ) AS mapping(dr_email, clinic_email)
         JOIN auth_users dru ON dru.email = mapping.dr_email
         JOIN users_providers pr ON pr.user_id = dru.id
         JOIN auth_users cu ON cu.email = mapping.clinic_email
         JOIN clinics c ON c.user_id = cu.id;

-- ---------------------------------------------------------------------
-- 7) Favoritos — Valeria marca a los 5 médicos como favoritos
-- ---------------------------------------------------------------------
INSERT INTO favorites (patient_id, provider_id, created_at)
SELECT p.id, pr.id, NOW() - (interval '1 day' * gs.n)
FROM users_patients p
         JOIN auth_users pu ON pu.id = p.user_id
         CROSS JOIN LATERAL (
    SELECT pr.id, ROW_NUMBER() OVER () AS n
    FROM users_providers pr
             JOIN auth_users prou ON prou.id = pr.user_id
    WHERE prou.email LIKE 'dr.%@newclinics.test'
        ) AS gs(id, n)
         JOIN users_providers pr ON pr.id = gs.id
WHERE pu.email = 'valeria.chumpitaz@newclinics.test';

-- ---------------------------------------------------------------------
-- 8) Historial de búsquedas — para la pantalla "Mis búsquedas"
--    Incluye registros NO guardados (pestaña Historial) y guardados
--    (pestaña Guardados, requiere el campo "saved" del backend nuevo).
-- ---------------------------------------------------------------------
INSERT INTO search_history (user_id, keyword, min_price, max_price, rating, specialty, district, saved, created_at)
SELECT id, 'Cardiólogos en Lima', NULL, NULL, 4.0, 'Cardiología', 'Surquillo', false, NOW() - interval '2 hours'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO search_history (user_id, keyword, min_price, max_price, rating, specialty, district, saved, created_at)
SELECT id, 'Pediatras online', NULL, NULL, NULL, 'Pediatría', NULL, false, NOW() - interval '3 days'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO search_history (user_id, keyword, min_price, max_price, rating, specialty, district, saved, created_at)
SELECT id, 'Dermatología en Miraflores', NULL, NULL, NULL, 'Dermatología', 'Miraflores', false, NOW() - interval '5 days'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO search_history (user_id, keyword, min_price, max_price, rating, specialty, district, saved, created_at)
SELECT id, 'Oftalmología en San Isidro', NULL, NULL, NULL, 'Oftalmología', 'San Isidro', false, NOW() - interval '7 days'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

-- Guardadas (pestaña "Guardados")
INSERT INTO search_history (user_id, keyword, min_price, max_price, rating, specialty, district, saved, created_at)
SELECT id, 'Traumatólogos en San Borja', NULL, NULL, NULL, 'Traumatología', 'San Borja', true, NOW() - interval '1 day'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO search_history (user_id, keyword, min_price, max_price, rating, specialty, district, saved, created_at)
SELECT id, 'Ginecólogos con ecografía 4D', NULL, NULL, NULL, 'Ginecología', 'San Borja', true, NOW() - interval '3 days'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO search_history (user_id, keyword, min_price, max_price, rating, specialty, district, saved, created_at)
SELECT id, 'Clínicas con emergencia pediátrica', NULL, NULL, NULL, 'Pediatría', NULL, true, NOW() - interval '5 days'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO search_history (user_id, keyword, min_price, max_price, rating, specialty, district, saved, created_at)
SELECT id, 'Nutricionistas deportivos', NULL, NULL, NULL, NULL, 'La Molina', true, NOW() - interval '7 days'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO search_history (user_id, keyword, min_price, max_price, rating, specialty, district, saved, created_at)
SELECT id, 'Psiquiatras con terapia online', NULL, NULL, NULL, NULL, NULL, true, NOW() - interval '14 days'
FROM auth_users WHERE email = 'valeria.chumpitaz@newclinics.test';

-- ---------------------------------------------------------------------
-- 9) Citas — próximas, de HOY, pasadas (con amount_paid) y canceladas
-- ---------------------------------------------------------------------

-- Cita de HOY (para que el dashboard muestre "Citas del día")
INSERT INTO appointments_appointments
(patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, notes, created_at)
SELECT p.id, pr.id, 'Consulta general', CURRENT_DATE, TIME '09:00', TIME '09:30', 'CONFIRMED',
       'Cita confirmada para hoy', NOW()
FROM users_patients p
         JOIN auth_users pu ON pu.id = p.user_id
         JOIN users_providers pr ON pr.user_id = (SELECT id FROM auth_users WHERE email = 'dr.vitalis@newclinics.test')
WHERE pu.email = 'valeria.chumpitaz@newclinics.test';

-- Próxima (dentro de 5 días)
INSERT INTO appointments_appointments
(patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, notes, created_at)
SELECT p.id, pr.id, 'Consulta cardiológica', CURRENT_DATE + 5, TIME '10:30', TIME '11:00', 'SCHEDULED',
       'Primera consulta', NOW()
FROM users_patients p
         JOIN auth_users pu ON pu.id = p.user_id
         JOIN users_providers pr ON pr.user_id = (SELECT id FROM auth_users WHERE email = 'dr.aurora@newclinics.test')
WHERE pu.email = 'valeria.chumpitaz@newclinics.test';

-- Completadas (con amount_paid, para "Ingresos del mes" y reseñas)
INSERT INTO appointments_appointments
(patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, amount_paid, notes, created_at)
SELECT p.id, pr.id, 'Consulta general', CURRENT_DATE - 2, TIME '09:00', TIME '09:30', 'COMPLETED', 130.00,
       'Chequeo de rutina', NOW() - interval '2 days'
FROM users_patients p
    JOIN auth_users pu ON pu.id = p.user_id
    JOIN users_providers pr ON pr.user_id = (SELECT id FROM auth_users WHERE email = 'dr.vitalis@newclinics.test')
WHERE pu.email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO appointments_appointments
(patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, amount_paid, notes, created_at)
SELECT p.id, pr.id, 'Consulta cardiológica', CURRENT_DATE - 4, TIME '11:00', TIME '11:30', 'COMPLETED', 170.00,
       'Control cardiológico', NOW() - interval '4 days'
FROM users_patients p
    JOIN auth_users pu ON pu.id = p.user_id
    JOIN users_providers pr ON pr.user_id = (SELECT id FROM auth_users WHERE email = 'dr.aurora@newclinics.test')
WHERE pu.email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO appointments_appointments
(patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, amount_paid, notes, created_at)
SELECT p.id, pr.id, 'Consulta oftalmológica', CURRENT_DATE - 6, TIME '14:00', TIME '14:30', 'COMPLETED', 160.00,
       'Revisión de vista', NOW() - interval '6 days'
FROM users_patients p
    JOIN auth_users pu ON pu.id = p.user_id
    JOIN users_providers pr ON pr.user_id = (SELECT id FROM auth_users WHERE email = 'dr.novavision@newclinics.test')
WHERE pu.email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO appointments_appointments
(patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, amount_paid, notes, created_at)
SELECT p.id, pr.id, 'Consulta ginecológica', CURRENT_DATE - 8, TIME '16:00', TIME '16:30', 'COMPLETED', 175.00,
       'Control anual', NOW() - interval '8 days'
FROM users_patients p
    JOIN auth_users pu ON pu.id = p.user_id
    JOIN users_providers pr ON pr.user_id = (SELECT id FROM auth_users WHERE email = 'dr.andes@newclinics.test')
WHERE pu.email = 'valeria.chumpitaz@newclinics.test';

INSERT INTO appointments_appointments
(patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, amount_paid, notes, created_at)
SELECT p.id, pr.id, 'Consulta general', CURRENT_DATE - 9, TIME '08:30', TIME '09:00', 'COMPLETED', 140.00,
       'Chequeo de rutina', NOW() - interval '9 days'
FROM users_patients p
    JOIN auth_users pu ON pu.id = p.user_id
    JOIN users_providers pr ON pr.user_id = (SELECT id FROM auth_users WHERE email = 'dr.bienestar@newclinics.test')
WHERE pu.email = 'valeria.chumpitaz@newclinics.test';

-- Cancelada
INSERT INTO appointments_appointments
(patient_id, provider_id, service_name, appointment_date, start_time, end_time, status, notes, created_at)
SELECT p.id, pr.id, 'Ecografía 4D', CURRENT_DATE - 3, TIME '15:00', TIME '15:30', 'CANCELLED',
       'Cancelada por el paciente', NOW() - interval '4 days'
FROM users_patients p
    JOIN auth_users pu ON pu.id = p.user_id
    JOIN users_providers pr ON pr.user_id = (SELECT id FROM auth_users WHERE email = 'dr.andes@newclinics.test')
WHERE pu.email = 'valeria.chumpitaz@newclinics.test';

-- ---------------------------------------------------------------------
-- 10) Departamentos (departments) — uno por clínica
-- ---------------------------------------------------------------------
INSERT INTO departments (clinic_id, name, capacity, current_patients)
SELECT id, 'Medicina General', 15, 5 FROM clinics WHERE name = 'Clínica Vitalis'
UNION ALL
SELECT id, 'Cardiología', 20, 11 FROM clinics WHERE name = 'Centro Cardiológico Aurora'
UNION ALL
SELECT id, 'Oftalmología', 12, 4 FROM clinics WHERE name = 'Clínica Nova Visión'
UNION ALL
SELECT id, 'Ginecología', 18, 7 FROM clinics WHERE name = 'Policlínico Andes Salud'
UNION ALL
SELECT id, 'Medicina General', 10, 3 FROM clinics WHERE name = 'Clínica Bienestar Total';

-- ---------------------------------------------------------------------
-- 11) Reseñas (provider_reviews) — una por médico, sobre su cita
--     COMPLETED correspondiente.
-- ---------------------------------------------------------------------
INSERT INTO provider_reviews (provider_id, patient_id, appointment_id, rating, comment, created_at)
SELECT pr.id, p.id, a.id, 5, 'Excelente atención, muy puntual y profesional.', NOW() - interval '1 day'
FROM users_providers pr
    JOIN auth_users pru ON pru.id = pr.user_id
    JOIN appointments_appointments a ON a.provider_id = pr.id AND a.status = 'COMPLETED'
    JOIN users_patients p ON p.id = a.patient_id
WHERE pru.email = 'dr.vitalis@newclinics.test';

INSERT INTO provider_reviews (provider_id, patient_id, appointment_id, rating, comment, created_at)
SELECT pr.id, p.id, a.id, 5, 'Explicó todo con mucha claridad, muy recomendable.', NOW() - interval '3 days'
FROM users_providers pr
    JOIN auth_users pru ON pru.id = pr.user_id
    JOIN appointments_appointments a ON a.provider_id = pr.id AND a.status = 'COMPLETED'
    JOIN users_patients p ON p.id = a.patient_id
WHERE pru.email = 'dr.aurora@newclinics.test';

INSERT INTO provider_reviews (provider_id, patient_id, appointment_id, rating, comment, created_at)
SELECT pr.id, p.id, a.id, 4, 'Buena atención, aunque la espera fue un poco larga.', NOW() - interval '5 days'
FROM users_providers pr
    JOIN auth_users pru ON pru.id = pr.user_id
    JOIN appointments_appointments a ON a.provider_id = pr.id AND a.status = 'COMPLETED'
    JOIN users_patients p ON p.id = a.patient_id
WHERE pru.email = 'dr.novavision@newclinics.test';

INSERT INTO provider_reviews (provider_id, patient_id, appointment_id, rating, comment, created_at)
SELECT pr.id, p.id, a.id, 5, 'Muy atenta y clara con las indicaciones.', NOW() - interval '7 days'
FROM users_providers pr
    JOIN auth_users pru ON pru.id = pr.user_id
    JOIN appointments_appointments a ON a.provider_id = pr.id AND a.status = 'COMPLETED'
    JOIN users_patients p ON p.id = a.patient_id
WHERE pru.email = 'dr.andes@newclinics.test';

INSERT INTO provider_reviews (provider_id, patient_id, appointment_id, rating, comment, created_at)
SELECT pr.id, p.id, a.id, 4, 'Buen trato y diagnóstico certero.', NOW() - interval '8 days'
FROM users_providers pr
    JOIN auth_users pru ON pru.id = pr.user_id
    JOIN appointments_appointments a ON a.provider_id = pr.id AND a.status = 'COMPLETED'
    JOIN users_patients p ON p.id = a.patient_id
WHERE pru.email = 'dr.bienestar@newclinics.test';

COMMIT;

DELETE FROM offers WHERE service_id IN (
    SELECT id FROM catalog_services WHERE category_id IN (
        SELECT id FROM catalog_service_categories WHERE name IN ('Consultas', 'Diagnóstico', 'Procedimientos')
    )
);
DELETE FROM catalog_services WHERE category_id IN (
    SELECT id FROM catalog_service_categories WHERE name IN ('Consultas', 'Diagnóstico', 'Procedimientos')
);
DELETE FROM catalog_service_categories WHERE name IN ('Consultas', 'Diagnóstico', 'Procedimientos');

-- Categorías de servicios
INSERT INTO catalog_service_categories (name, description, is_active) VALUES
                                                                          ('Consultas', 'Consultas médicas generales y de especialidad', true),
                                                                          ('Diagnóstico', 'Exámenes y estudios de diagnóstico', true),
                                                                          ('Procedimientos', 'Procedimientos médicos ambulatorios', true);

-- Servicios (category_id resuelto por nombre, no por índice fijo)
INSERT INTO catalog_services (name, description, price, is_active, category_id)
SELECT 'Consulta general', 'Evaluación médica general', 80.00, true, id FROM catalog_service_categories WHERE name = 'Consultas'
UNION ALL
SELECT 'Consulta de especialidad', 'Consulta con médico especialista', 150.00, true, id FROM catalog_service_categories WHERE name = 'Consultas'
UNION ALL
SELECT 'Control post-operatorio', 'Seguimiento después de una cirugía', 100.00, true, id FROM catalog_service_categories WHERE name = 'Consultas'
UNION ALL
SELECT 'Electrocardiograma', 'Estudio de la actividad eléctrica del corazón', 60.00, true, id FROM catalog_service_categories WHERE name = 'Diagnóstico'
UNION ALL
SELECT 'Ecografía', 'Estudio de imagen por ultrasonido', 120.00, true, id FROM catalog_service_categories WHERE name = 'Diagnóstico'
UNION ALL
SELECT 'Análisis de laboratorio', 'Toma y análisis de muestras de sangre/orina', 70.00, true, id FROM catalog_service_categories WHERE name = 'Diagnóstico'
UNION ALL
SELECT 'Curación de heridas', 'Limpieza y curación de heridas menores', 50.00, true, id FROM catalog_service_categories WHERE name = 'Procedimientos'
UNION ALL
SELECT 'Extracción de lunares', 'Procedimiento ambulatorio de remoción', 180.00, true, id FROM catalog_service_categories WHERE name = 'Procedimientos';
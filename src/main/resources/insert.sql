-- =====================================================================
-- insert.sql — Seed de datos de prueba coherentes para ComparaSalud.
--
-- Idempotente: se puede correr varias veces sin duplicar filas (usa
-- WHERE NOT EXISTS / subconsultas por clave natural en vez de asumir
-- nombres de constraints).
--
-- Requiere que el backend haya arrancado al menos una vez antes (para
-- que Hibernate cree el esquema vía ddl-auto=update).
--
-- Cómo correrlo:
--   psql -h localhost -p 5432 -U postgres -d db_ComparaSalud -f insert.sql
--
-- Todas las cuentas de prueba (pacientes y proveedores) usan la
-- contraseña: password123
-- =====================================================================


-- ── 1. Especialidades (catalog_specialties) ─────────────────────────────
INSERT INTO catalog_specialties (name, description, is_active)
SELECT v.name, v.description, true FROM (VALUES
  ('Medicina General', 'Atención médica general y preventiva'),
  ('Cardiología', 'Diagnóstico y tratamiento de enfermedades del corazón'),
  ('Pediatría', 'Atención médica para niños y adolescentes'),
  ('Odontología', 'Salud dental y bucal'),
  ('Oftalmología', 'Salud visual y ocular'),
  ('Dermatología', 'Salud de la piel'),
  ('Traumatología', 'Lesiones y enfermedades del sistema musculoesquelético'),
  ('Ginecología', 'Salud reproductiva femenina'),
  ('Psicología', 'Salud mental y bienestar emocional'),
  ('Nutrición', 'Asesoría nutricional y control de peso')
) AS v(name, description)
WHERE NOT EXISTS (SELECT 1 FROM catalog_specialties c WHERE c.name = v.name);


-- ── 2. Clínicas ──────────────────────────────────────────────────────────
INSERT INTO clinics (
  name, description, phone, email, street, district, city, country, is_active,
  rating, review_count, emergencia_24h, estacionamiento, farmacia, laboratorio,
  imagenologia, servicio_ambulancia, unidad_cuidados_intensivos, hospitalizacion
)
SELECT v.* FROM (VALUES
  ('Clínica Ricardo Palma', 'Clínica privada de alta especialidad en Lima.', '014113000', 'contacto@ricardopalma.pe', 'Av. Javier Prado Este 1066', 'San Isidro', 'Lima', 'Perú', true, 4.6, 320, true, true, true, true, true, true, true, true),
  ('Clínica San Felipe', 'Atención médica integral para toda la familia.', '012192200', 'contacto@sanfelipe.pe', 'Av. Gregorio Escobedo 650', 'Jesús María', 'Lima', 'Perú', true, 4.4, 210, true, true, true, true, true, false, true, true),
  ('Clínica Delgado', 'Clínica boutique especializada en consulta ambulatoria.', '012106900', 'contacto@clinicadelgado.pe', 'Av. Angamos Oeste 675', 'Miraflores', 'Lima', 'Perú', true, 4.7, 180, false, true, true, true, false, false, false, false),
  ('Hospital Rebagliati', 'Hospital de referencia nacional de EsSalud.', '012650000', 'contacto@rebagliati.gob.pe', 'Av. Rebagliati 490', 'Jesús María', 'Lima', 'Perú', true, 4.1, 540, true, true, true, true, true, true, true, true),
  ('Clínica San Pablo', 'Red de clínicas privadas con cobertura nacional.', '012133000', 'contacto@sanpablo.pe', 'Av. El Corregidor 1801', 'La Molina', 'Lima', 'Perú', true, 4.5, 260, true, true, true, true, true, true, true, true),
  ('Clínica Internacional', 'Atención médica privada con tecnología de punta.', '016190000', 'contacto@clinicainternacional.pe', 'Av. Garcilaso de la Vega 1420', 'Cercado de Lima', 'Lima', 'Perú', true, 4.3, 190, true, true, true, true, true, false, true, true)
) AS v(name, description, phone, email, street, district, city, country, is_active, rating, review_count, emergencia_24h, estacionamiento, farmacia, laboratorio, imagenologia, servicio_ambulancia, unidad_cuidados_intensivos, hospitalizacion)
WHERE NOT EXISTS (SELECT 1 FROM clinics c WHERE c.name = v.name);


-- ── 3. Proveedores (auth_users + users_providers) ───────────────────────
-- Password para todos: password123
-- Hash generado con el mismo BCryptPasswordEncoder que usa el backend.

INSERT INTO auth_users (email, password, role_id, is_verified, created_at, failed_login_attempts)
SELECT v.email, '$2a$10$3vix56JfWkRuosOGLUYoheDYisDr8onteTIF4UFo3G9tykZalGHdq', 2, true, now(), 0
FROM (VALUES
  ('carlos.mendez@comparasalud.pe'),
  ('maura.casas@comparasalud.pe'),
  ('lucia.fernandez@comparasalud.pe'),
  ('armando.castillo@comparasalud.pe'),
  ('mateo.rojas@comparasalud.pe'),
  ('luna.yi@comparasalud.pe'),
  ('rosa.delgado@comparasalud.pe'),
  ('jorge.salinas@comparasalud.pe')
) AS v(email)
WHERE NOT EXISTS (SELECT 1 FROM auth_users a WHERE a.email = v.email);

INSERT INTO users_providers (
  user_id, full_name, phone, specialty, description, rating, is_validated,
  price_per_appointment, average_rating, experience_years, street, district, city, country,
  modality, duration_minutes
)
SELECT a.id, v.full_name, v.phone, v.specialty, v.description, v.rating, true,
       v.price, v.rating, v.experience_years, v.street, v.district, v.city, 'Perú',
       'Presencial', 30
FROM (VALUES
  ('carlos.mendez@comparasalud.pe', 'Dr. Carlos Méndez', '987654321', 'Cardiología', 'Especialista en cardiología con enfoque en prevención cardiovascular.', 4.8, 150.00, 15, 'Av. Javier Prado Este 1066', 'San Isidro', 'Lima'),
  ('maura.casas@comparasalud.pe', 'Dra. Maura Casas', '987654322', 'Pediatría', 'Pediatra con experiencia en control de niño sano y vacunación.', 4.6, 120.00, 9, 'Av. Gregorio Escobedo 650', 'Jesús María', 'Lima'),
  ('lucia.fernandez@comparasalud.pe', 'Dra. Lucía Fernández', '987654323', 'Odontología', 'Odontóloga general especializada en estética dental.', 4.9, 90.00, 7, 'Av. Angamos Oeste 675', 'Miraflores', 'Lima'),
  ('armando.castillo@comparasalud.pe', 'Dr. Armando Castillo', '987654324', 'Oftalmología', 'Oftalmólogo especialista en cirugía refractiva.', 4.7, 130.00, 12, 'Av. Javier Prado Este 1066', 'San Isidro', 'Lima'),
  ('mateo.rojas@comparasalud.pe', 'Dr. Mateo Rojas', '987654325', 'Cardiología', 'Cardiólogo intervencionista con especialización en cateterismo cardíaco.', 4.5, 160.00, 10, 'Av. El Corregidor 1801', 'La Molina', 'Lima'),
  ('luna.yi@comparasalud.pe', 'Dra. Luna Yi', '987654326', 'Psicología', 'Psicóloga clínica especializada en terapias de ansiedad y estrés.', 4.4, 110.00, 6, 'Av. Garcilaso de la Vega 1420', 'Cercado de Lima', 'Lima'),
  ('rosa.delgado@comparasalud.pe', 'Dra. Rosa Delgado', '987654327', 'Ginecología', 'Ginecóloga con enfoque en salud reproductiva integral.', 4.6, 140.00, 11, 'Av. Gregorio Escobedo 650', 'Jesús María', 'Lima'),
  ('jorge.salinas@comparasalud.pe', 'Dr. Jorge Salinas', '987654328', 'Traumatología', 'Traumatólogo especializado en lesiones deportivas.', 4.3, 100.00, 14, 'Av. Rebagliati 490', 'Jesús María', 'Lima')
) AS v(email, full_name, phone, specialty, description, rating, price, experience_years, street, district, city)
JOIN auth_users a ON a.email = v.email
WHERE NOT EXISTS (SELECT 1 FROM users_providers p WHERE p.user_id = a.id);


-- ── 4. Proveedor ↔ Clínica (provider_clinics) ───────────────────────────
INSERT INTO provider_clinics (provider_id, clinic_id)
SELECT p.id, c.id
FROM (VALUES
  ('carlos.mendez@comparasalud.pe', 'Clínica Ricardo Palma'),
  ('carlos.mendez@comparasalud.pe', 'Clínica San Felipe'),
  ('maura.casas@comparasalud.pe', 'Clínica San Felipe'),
  ('lucia.fernandez@comparasalud.pe', 'Clínica Delgado'),
  ('armando.castillo@comparasalud.pe', 'Clínica Ricardo Palma'),
  ('armando.castillo@comparasalud.pe', 'Clínica Delgado'),
  ('mateo.rojas@comparasalud.pe', 'Clínica San Pablo'),
  ('luna.yi@comparasalud.pe', 'Clínica Internacional'),
  ('rosa.delgado@comparasalud.pe', 'Clínica San Felipe'),
  ('jorge.salinas@comparasalud.pe', 'Hospital Rebagliati')
) AS v(email, clinic_name)
JOIN auth_users a ON a.email = v.email
JOIN users_providers p ON p.user_id = a.id
JOIN clinics c ON c.name = v.clinic_name
WHERE NOT EXISTS (
  SELECT 1 FROM provider_clinics pc WHERE pc.provider_id = p.id AND pc.clinic_id = c.id
);


-- ── 5. Precios por especialidad y clínica (comparador de precios) ───────
INSERT INTO clinic_specialty_prices (clinic_id, specialty, price, duration_minutes)
SELECT c.id, v.specialty, v.price, v.duration_minutes
FROM (VALUES
  ('Clínica Ricardo Palma', 'Medicina General', 150.00, 50),
  ('Clínica Ricardo Palma', 'Cardiología', 220.00, 60),
  ('Clínica Ricardo Palma', 'Pediatría', 180.00, 50),
  ('Clínica Ricardo Palma', 'Oftalmología', 160.00, 45),
  ('Clínica San Felipe', 'Medicina General', 160.00, 60),
  ('Clínica San Felipe', 'Pediatría', 190.00, 60),
  ('Clínica San Felipe', 'Cardiología', 200.00, 50),
  ('Clínica San Felipe', 'Ginecología', 170.00, 50),
  ('Clínica Delgado', 'Odontología', 120.00, 45),
  ('Clínica Delgado', 'Oftalmología', 150.00, 45),
  ('Hospital Rebagliati', 'Medicina General', 120.00, 45),
  ('Hospital Rebagliati', 'Traumatología', 190.00, 45),
  ('Hospital Rebagliati', 'Cardiología', 200.00, 50),
  ('Clínica San Pablo', 'Cardiología', 250.00, 60),
  ('Clínica San Pablo', 'Medicina General', 140.00, 45),
  ('Clínica Internacional', 'Medicina General', 180.00, 45),
  ('Clínica Internacional', 'Psicología', 130.00, 50)
) AS v(clinic_name, specialty, price, duration_minutes)
JOIN clinics c ON c.name = v.clinic_name
WHERE NOT EXISTS (
  SELECT 1 FROM clinic_specialty_prices csp
  WHERE csp.clinic_id = c.id AND csp.specialty = v.specialty
);


-- ── 6. Categorías y catálogo de servicios (dashboard) ───────────────────
INSERT INTO catalog_service_categories (name, description, is_active)
SELECT v.name, v.description, true FROM (VALUES
  ('Vacunación', 'Aplicación de vacunas para niños y adultos'),
  ('Odontología', 'Servicios dentales generales'),
  ('Oftalmología', 'Exámenes y cuidado de la vista'),
  ('Cardiología', 'Chequeos y estudios cardiovasculares'),
  ('Imagenología', 'Estudios de imágenes diagnósticas'),
  ('Laboratorio', 'Análisis clínicos')
) AS v(name, description)
WHERE NOT EXISTS (SELECT 1 FROM catalog_service_categories c WHERE c.name = v.name);

INSERT INTO catalog_services (name, description, price, is_active, category_id)
SELECT v.name, v.description, v.price, true, c.id
FROM (VALUES
  ('Vacunación', 'Aplicación de vacunas según calendario nacional e internacional.', 12.00, 'Vacunación'),
  ('Limpieza dental', 'Limpieza y profilaxis dental completa.', 22.00, 'Odontología'),
  ('Examen visual', 'Evaluación completa de agudeza visual y salud ocular.', 23.00, 'Oftalmología'),
  ('Check up cardiovascular', 'Chequeo preventivo del corazón y sistema circulatorio.', 13.00, 'Cardiología'),
  ('Radiografía', 'Estudio de imagen por rayos X.', 21.00, 'Imagenología'),
  ('Análisis de sangre', 'Perfil bioquímico y hemograma completo.', 26.00, 'Laboratorio')
) AS v(name, description, price, category_name)
JOIN catalog_service_categories c ON c.name = v.category_name
WHERE NOT EXISTS (SELECT 1 FROM catalog_services s WHERE s.name = v.name);


-- ── 7. Ofertas exclusivas (descuento sobre un servicio real) ────────────
INSERT INTO offers (service_id, discount_percent, image_url, is_active, expires_at)
SELECT s.id, v.discount_percent, v.image_url, true, NULL
FROM (VALUES
  ('Vacunación', 20.00, 'assets/images/oferta1.png'),
  ('Limpieza dental', 15.00, 'assets/images/oferta2.png'),
  ('Análisis de sangre', 10.00, 'assets/images/oferta3.png'),
  ('Examen visual', 25.00, 'assets/images/oferta4.png'),
  ('Check up cardiovascular', 30.00, 'assets/images/oferta5.png'),
  ('Radiografía', 12.00, 'assets/images/oferta6.png')
) AS v(service_name, discount_percent, image_url)
JOIN catalog_services s ON s.name = v.service_name
WHERE NOT EXISTS (SELECT 1 FROM offers o WHERE o.service_id = s.id);


-- ── 8. Paciente de prueba ────────────────────────────────────────────────
INSERT INTO auth_users (email, password, role_id, is_verified, created_at, failed_login_attempts)
SELECT 'marisol.gomez@comparasalud.pe', '$2a$10$3vix56JfWkRuosOGLUYoheDYisDr8onteTIF4UFo3G9tykZalGHdq', 1, true, now(), 0
WHERE NOT EXISTS (SELECT 1 FROM auth_users WHERE email = 'marisol.gomez@comparasalud.pe');

INSERT INTO users_patients (user_id, name, phone, birthday, country)
SELECT a.id, 'Marisol Gomez', '955512345', '1994-03-15', 'Perú'
FROM auth_users a
WHERE a.email = 'marisol.gomez@comparasalud.pe'
AND NOT EXISTS (SELECT 1 FROM users_patients pt WHERE pt.user_id = a.id);


-- ── 9. Citas de prueba para el paciente (Mis citas) ─────────────────────
-- 2 próximas confirmadas, 1 completada, 1 cancelada — mismo patrón que ya
-- viste en las capturas del dashboard / mis citas.
INSERT INTO appointments_appointments (
  patient_id, provider_id, service_name, appointment_date, start_time, end_time,
  status, notes, payment_method, amount_paid, created_at
)
SELECT pt.id, pv.id, v.service_name, v.appt_date, v.start_time, v.end_time,
       v.status, v.notes, v.payment_method, v.amount_paid, now()
FROM (VALUES
  ('carlos.mendez@comparasalud.pe', 'Cardiología', DATE '2026-07-14', TIME '11:00', TIME '11:30', 'SCHEDULED', 'Control de rutina', 'Tarjeta', 150.00),
  ('carlos.mendez@comparasalud.pe', 'Cardiología', DATE '2026-07-21', TIME '10:00', TIME '10:30', 'SCHEDULED', 'Seguimiento de tratamiento', 'Tarjeta', 150.00),
  ('maura.casas@comparasalud.pe', 'Pediatría', DATE '2026-06-10', TIME '09:00', TIME '09:30', 'COMPLETED', 'Control de niño sano', 'Tarjeta', 120.00),
  ('armando.castillo@comparasalud.pe', 'Oftalmología', DATE '2026-06-20', TIME '15:00', TIME '15:30', 'CANCELLED', 'Revisión anual', 'PayPal', 130.00)
) AS v(provider_email, service_name, appt_date, start_time, end_time, status, notes, payment_method, amount_paid)
JOIN auth_users pu ON pu.email = v.provider_email
JOIN users_providers pv ON pv.user_id = pu.id
JOIN auth_users patu ON patu.email = 'marisol.gomez@comparasalud.pe'
JOIN users_patients pt ON pt.user_id = patu.id
WHERE NOT EXISTS (
  SELECT 1 FROM appointments_appointments ap
  WHERE ap.patient_id = pt.id AND ap.provider_id = pv.id
  AND ap.appointment_date = v.appt_date AND ap.start_time = v.start_time
);

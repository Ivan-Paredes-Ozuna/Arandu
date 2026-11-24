-- Datos demo para Arandú (IHC / FPUNA)
-- Usuario: ana@arandu.edu.py / arandu123

INSERT INTO users (id, email, password_hash, full_name, created_at) VALUES
(1, 'ana@arandu.edu.py', '$2b$10$nE4Q7Do/T9Zf8ZjggnqfreiRSUW8W9wH/I8sB9tQe4.Uhj/Y1ci3m', 'Ana Demo', NOW());

INSERT INTO courses (id, title, description, level, duration_hours, modality, rating, unit_count, category) VALUES
(1, 'Bases de Datos',
 'Aprendé modelado relacional, SQL y consultas con JOIN en bloques cortos que se adaptan a tu semana.',
 'Intermedio', 12, 'Autónomo + tutorías', 4.7, 6, 'Datos'),
(2, 'Python para automatizar',
 'Scripts prácticos para organizar tareas académicas y laborales en sesiones de 10 a 30 minutos.',
 'Principiante', 8, 'Autónomo', 4.5, 5, 'Programación'),
(3, 'Diseño de interfaces',
 'Principios de usabilidad, heurísticas y prototipado aplicados a productos educativos.',
 'Intermedio', 10, 'Autónomo + mentoría', 4.6, 5, 'Diseño');

INSERT INTO activities (id, course_id, unit_number, title, type, duration_minutes, order_index, summary) VALUES
(1, 1, 1, 'Modelo relacional', 'video', 12, 1, 'Conceptos de entidades, atributos y relaciones para empezar el curso.'),
(2, 1, 1, 'Ejercicio: claves y relaciones', 'ejercicio', 15, 2, 'Práctica breve para identificar claves primarias y foráneas.'),
(3, 1, 2, 'Consultas SELECT', 'video', 10, 3, 'Filtrar y proyectar datos con SELECT y WHERE.'),
(4, 1, 3, 'Agregaciones', 'practica', 20, 4, 'GROUP BY y funciones de agregación en casos reales.'),
(5, 1, 4, 'JOIN en SQL', 'video', 12, 5, 'Reanudación de INNER JOIN y LEFT JOIN con ejemplos.'),
(6, 1, 4, 'Práctica de JOIN', 'practica', 30, 6, 'Resolver consultas que combinan varias tablas.'),
(7, 2, 1, 'Variables y flujos', 'video', 10, 1, 'Fundamentos para automatizar tareas.'),
(8, 2, 2, 'Archivos y carpetas', 'practica', 20, 2, 'Organizar materiales de estudio con scripts.'),
(9, 2, 3, 'APIs simples', 'ejercicio', 15, 3, 'Consumir datos externos en bloques cortos.'),
(10, 3, 1, 'Heurísticas de Nielsen', 'video', 12, 1, 'Visibilidad del estado y prevención de errores.'),
(11, 3, 2, 'Evaluación heurística', 'practica', 25, 2, 'Inspeccionar una interfaz y priorizar hallazgos.');

INSERT INTO help_topics (id, course_id, question, answer, tags) VALUES
(1, 1, '¿Cuándo usar INNER JOIN?',
 'Usá INNER JOIN cuando solo necesitás filas que existen en ambas tablas. Si una fila no tiene pareja, no aparece.',
 'join,sql,inner'),
(2, 1, '¿Qué diferencia hay con LEFT JOIN?',
 'LEFT JOIN conserva todas las filas de la tabla izquierda y completa con NULL cuando no hay coincidencia a la derecha.',
 'join,sql,left'),
(3, 1, 'Me confundo con las claves foráneas',
 'Una clave foránea apunta a la clave primaria de otra tabla y expresa la relación entre entidades.',
 'modelo,claves'),
(4, 2, '¿Por dónde retomo si abandoné una semana?',
 'Abrí Mi progreso y usá Retomar ahora: te lleva a la última actividad incompleta sin buscar manualmente.',
 'ritmo,progreso');

-- Ana: Bases de Datos al 42%, 5 días inactiva, retoma JOIN (unidad 4)
INSERT INTO enrollments (id, user_id, course_id, status, progress_percent, last_activity_id, enrolled_at, last_accessed_at) VALUES
(1, 1, 1, 'INSCRITO', 42, 5, NOW() - INTERVAL '20 days', NOW() - INTERVAL '5 days');

INSERT INTO activity_progress (enrollment_id, activity_id, percent_watched, completed, updated_at) VALUES
(1, 1, 100, TRUE,  NOW() - INTERVAL '18 days'),
(1, 2, 100, TRUE,  NOW() - INTERVAL '16 days'),
(1, 3, 100, TRUE,  NOW() - INTERVAL '12 days'),
(1, 4, 100, TRUE,  NOW() - INTERVAL '8 days'),
(1, 5, 35,  FALSE, NOW() - INTERVAL '5 days'),
(1, 6, 0,   FALSE, NOW() - INTERVAL '5 days');

-- Alinear secuencias tras IDs explícitos
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT COALESCE(MAX(id), 1) FROM users));
SELECT setval(pg_get_serial_sequence('courses', 'id'), (SELECT COALESCE(MAX(id), 1) FROM courses));
SELECT setval(pg_get_serial_sequence('activities', 'id'), (SELECT COALESCE(MAX(id), 1) FROM activities));
SELECT setval(pg_get_serial_sequence('help_topics', 'id'), (SELECT COALESCE(MAX(id), 1) FROM help_topics));
SELECT setval(pg_get_serial_sequence('enrollments', 'id'), (SELECT COALESCE(MAX(id), 1) FROM enrollments));
SELECT setval(pg_get_serial_sequence('activity_progress', 'id'), (SELECT COALESCE(MAX(id), 1) FROM activity_progress));

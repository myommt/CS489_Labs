select * from dentists;

select CONCAT(dentists.FirstName,' ',dentists.lastName) as 'dentistName',
patients.patient_id as 'patNo',
CONCAT(patients.FirstName,' ',patients.LastName) as 'patName',
appointments.appointmentDateTime,
appointments.surgerylocation
FROM appointments
INNER JOIN dentists
ON appointments.dentist_id=dentists.dentist_id
INNER JOIN patients
ON appointments.patient_id=patients.patient_id 
WHERE dentists.dentist_id=1;

select CONCAT(dentists.FirstName,' ',dentists.lastName) as 'dentistName',
patients.patient_id as 'patNo',
CONCAT(patients.FirstName,' ',patients.LastName) as 'patName',
appointments.appointmentDateTime,
appointments.surgerylocation,
surgerylocations.*
FROM appointments
INNER JOIN dentists
ON appointments.dentist_id=dentists.dentist_id
INNER JOIN patients
ON appointments.patient_id=patients.patient_id 
INNER JOIN surgerylocations
ON appointments.surgerylocation=surgerylocations.surgerylocation_id
WHERE surgerylocation=2;


select CONCAT(dentists.FirstName,' ',dentists.lastName) as 'dentistName',
patients.patient_id as 'patNo',
CONCAT(patients.FirstName,' ',patients.LastName) as 'patName',
appointments.appointmentDateTime,
appointments.surgerylocation,
surgerylocations.*
FROM appointments
INNER JOIN dentists
ON appointments.dentist_id=dentists.dentist_id
INNER JOIN patients
ON appointments.patient_id=patients.patient_id 
INNER JOIN surgerylocations
ON appointments.surgerylocation=surgerylocations.surgerylocation_id
WHERE patients.patient_id=1
AND appointmentDateTime = '2025-10-22'


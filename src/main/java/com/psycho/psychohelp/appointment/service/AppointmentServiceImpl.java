package com.psycho.psychohelp.appointment.service;

import com.psycho.psychohelp.appointment.domain.model.entity.Appointment;
import com.psycho.psychohelp.appointment.domain.persistance.AppointmentRepository;
import com.psycho.psychohelp.appointment.domain.service.AppointmentService;
import com.psycho.psychohelp.shared.exception.ResourceNotFoundException;
import com.psycho.psychohelp.shared.exception.ResourceValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final static String ENTITY = "Appointment";

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private Validator validator;

    @Override
    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public Page<Appointment> getAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    @Override
    public Appointment getById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY, appointmentId));
    }

    @Override
    public Appointment create(Appointment request) {
        Set<ConstraintViolation<Appointment>> violations = validator.validate(request);

        if(!violations.isEmpty())
            throw new ResourceValidationException(ENTITY, violations);

        return appointmentRepository.save(request);
    }

    @Override
    public Appointment update(Long appointmentId, Appointment request) {
        Set<ConstraintViolation<Appointment>> violations = validator.validate(request);

        if(!violations.isEmpty())
            throw new ResourceValidationException(ENTITY, violations);

        return appointmentRepository.findById(appointmentId).map(appointment ->
                        appointmentRepository.save(
                                appointment.withPsychoNotes(request.getPsychoNotes())
                                        .withScheduleDate(request.getScheduleDate())
                                        .withCreatedDate(request.getCreatedDate())))
                .orElseThrow(() -> new ResourceNotFoundException(ENTITY, appointmentId));
    }

    @Override
    public Appointment getByPsychologistName(String firstName, String lastName) {
        return appointmentRepository.findByPsychologistName(firstName, lastName);
    }

    @Override
    public Appointment getByTopic(String topic) {
        return appointmentRepository.findByTopic(topic);
    }

    @Override
    public ResponseEntity<?> delete(Long appointmentId) {
        return appointmentRepository.findById(appointmentId).map(appointment -> {
            appointmentRepository.delete(appointment);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException(ENTITY, appointmentId));
    }
}

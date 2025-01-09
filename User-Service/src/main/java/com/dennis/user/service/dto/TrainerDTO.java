package com.dennis.user.service.dto;

import com.dennis.user.service.model.Trainer;
import lombok.Data;

@Data
public class TrainerDTO extends Trainer {
    private String photo;
    public TrainerDTO(Trainer trainer, String base64Photo){
        this.setFirstName(trainer.getFirstName());
        this.setLastName(trainer.getLastName());
        this.setEmail(trainer.getEmail());
        this.setRole(trainer.getRole());
        this.setStatus(trainer.getStatus());
        this.setGender(trainer.getGender());
        this.setCountry(trainer.getCountry());
        this.setPhoneNumber(trainer.getPhoneNumber());
        this.setAssignSpecialization(trainer.getAssignSpecialization());
        this.photo = base64Photo;
    }
}
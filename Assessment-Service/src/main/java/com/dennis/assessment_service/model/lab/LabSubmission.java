package com.dennis.assessment_service.model.lab;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabSubmission {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Trainee email is required")
    @Email(message = "Invalid email format")
    private String traineeEmail;

    @ElementCollection
    @CollectionTable(name = "lab_submission_links", joinColumns = @JoinColumn(name = "lab_submission_id"))
    @Column(name = "link")
    @NotEmpty(message = "At least one submission link is required")
    private List<@NotBlank @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format") String> submissionLinks;
    @NotNull
    private int labWeek;
    @NotNull(message = "Submission time is required")
    private LocalDateTime submittedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "lab_id")
    @JsonBackReference
    private Lab lab;
}


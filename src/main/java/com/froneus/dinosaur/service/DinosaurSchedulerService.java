package com.froneus.dinosaur.service;

import com.froneus.dinosaur.model.Dinosaur;
import com.froneus.dinosaur.model.Status;
import com.froneus.dinosaur.repository.DinosaurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DinosaurSchedulerService {

    private final DinosaurRepository dinosaurRepository;

    @Scheduled(cron = "${scheduler.endangered.cron}")
    public void updateEndangeredStatus() {
        log.info("Running scheduler to update dinosaurs to ENDANGERED status.");
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursFromNow = now.plusHours(24);

        List<Dinosaur> dinosaursToUpdate = dinosaurRepository.findByStatusAndExtinctionDateBefore(Status.ALIVE, twentyFourHoursFromNow);

        for (Dinosaur dinosaur : dinosaursToUpdate) {
            // Only update if extinction date is in the future
            if (dinosaur.getExtinctionDate().isAfter(now)) {
                dinosaur.setStatus(Status.ENDANGERED);
                dinosaurRepository.save(dinosaur);
                log.info("Dinosaur {} (ID: {}) changed to ENDANGERED status.", dinosaur.getName(), dinosaur.getId());
            }
        }
    }

    @Scheduled(cron = "${scheduler.extinct.cron}")
    public void updateExtinctStatus() {
        log.info("Running scheduler to update dinosaurs to EXTINCT status.");
        LocalDateTime now = LocalDateTime.now();

        List<Dinosaur> dinosaursToUpdate = dinosaurRepository.findByExtinctionDateBeforeAndStatusNot(now, Status.EXTINCT);

        for (Dinosaur dinosaur : dinosaursToUpdate) {
            dinosaur.setStatus(Status.EXTINCT);
            dinosaurRepository.save(dinosaur);
            log.info("Dinosaur {} (ID: {}) changed to EXTINCT status.", dinosaur.getName(), dinosaur.getId());
        }
    }
}

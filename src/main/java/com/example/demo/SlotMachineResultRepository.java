package com.example.demo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.SlotMachineController.SlotMachineResult;

@Repository
public interface SlotMachineResultRepository extends MongoRepository<SlotMachineResult, String> {
    default int getNextAttemptNumber() {
        long count = count();
        return (int) count + 1;
    }

    long countByMessage(String message);
}

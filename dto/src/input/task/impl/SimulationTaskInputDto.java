package input.task.impl;

import input.task.TaskInputDto;

public class SimulationTaskInputDto implements TaskInputDto {
    final private Long processTime;
    final private Boolean isRandom, isScratch;
    final private Double probabilitySuccess, probabilityWarning;
    final private Integer amountOfThreads;

    public SimulationTaskInputDto(Long processTimeInput, Boolean isRandomInput,
                                  Double probabilitySuccessInput, Double probabilityWarningInput, Boolean isScratchInput,Integer amountOfThreadsInput) {
        processTime = processTimeInput;
        isRandom = isRandomInput;
        probabilitySuccess = probabilitySuccessInput;
        probabilityWarning = probabilityWarningInput;
        isScratch = isScratchInput;
        amountOfThreads=amountOfThreadsInput;
    }

    @Override
    final public Integer getAmountOfThreads() {
        return amountOfThreads;
    }

    final public Long getProcessTime() {
        return processTime;
    }

    final public Boolean getIsRandom() {
        return isRandom;
    }

    final public Double getProbabilitySuccess() {
        return probabilitySuccess;
    }

    final public Double getProbabilityWarning() {
        return probabilityWarning;
    }

    final public Boolean getIsScratch() {
        return isScratch;
    }
}

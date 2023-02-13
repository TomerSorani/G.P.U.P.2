package input.task.impl;
import input.task.TaskInputDto;

public class CompilationTaskInputDto implements TaskInputDto {
    final private Boolean isScratch;
    final private Integer amountOfThreads;
    final private String compilationSourceFolderPath,compilationDestFolderPath;//  src=from user ; dest=from user

     public CompilationTaskInputDto(Boolean isScratchInput, Integer amountOfThreadsInput, String compilationSourceFolderPathInput,
                                    String compilationDestFolderPathInput){
         isScratch=isScratchInput;
         amountOfThreads=amountOfThreadsInput;
         compilationSourceFolderPath=compilationSourceFolderPathInput;
         compilationDestFolderPath=compilationDestFolderPathInput;
     }
    @Override
    final public Boolean getIsScratch() {
        return isScratch;
    }
    @Override
    final public Integer getAmountOfThreads() {
        return amountOfThreads;
    }
    final public String getCompilationSourceFolderPath() {
        return compilationSourceFolderPath;
    }
    final public String getCompilationDestFolderPath() {
        return compilationDestFolderPath;
    }
}

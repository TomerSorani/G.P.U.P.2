package output;

public class AllGraphDto {
    private TargetInfoDto[] allTargetsDto;

    public AllGraphDto(TargetInfoDto[] allTargetsDtoIn){
        allTargetsDto=allTargetsDtoIn;
    }
    public TargetInfoDto[] getAllTargetsDto(){return allTargetsDto;}
}

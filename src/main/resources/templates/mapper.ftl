package ${packageName};

<#if hasMapStruct?? && hasMapStruct>
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ${entityClassName};
import ${dtoClassName};

@Mapper(componentModel = "spring")
public interface ${className} {

    ${className} INSTANCE = Mappers.getMapper(${className}.class);

    ${dtoSimpleName} toDTO(${entitySimpleName} entity);

    ${entitySimpleName} toEntity(${dtoSimpleName} dto);
}
<#else>
import ${entityClassName};
import ${dtoClassName};

public class ${className} {

    public static ${dtoClassName} toDTO(${entityClassName} entity) {
        if (entity == null) return null;

        ${dtoClassName} dto = new ${dtoClassName}();
        // TODO: Map fields manually
        // dto.setId(entity.getId());
        // dto.setName(entity.getName());

        return dto;
    }

    public static ${entityClassName} toEntity(${dtoClassName} dto) {
        if (dto == null) return null;

        ${entityClassName} entity = new ${entityClassName}();
        // TODO: Map fields manually
        // entity.setId(dto.getId());
        // entity.setName(dto.getName());

        return entity;
    }
}
</#if>
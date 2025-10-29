package ${packageName};

<#if hasCustomIdType && idType?upper_case == "UUID">
import java.util.UUID;
</#if>
import org.springframework.data.jpa.repository.JpaRepository;
import ${entityPackage}.${entityName};

public interface ${className} extends JpaRepository<${className}, ${idType}> {
}

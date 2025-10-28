package ${packageName};

<#list fields as f>
<#if f.type == "UUID">
import java.util.UUID;
</#if>
</#list>

public record ${className}(
<#list fields as f>
    ${f.type} ${f.name}<#if f_has_next>,</#if>
</#list>
) {}

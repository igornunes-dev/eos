package ${package};

<#-- Imports organizados -->
<#list imports as import>
import ${import};
</#list>

public class ${className}Factory {
    <#assign entityClass = entityClassName!className>
    <#assign lowerEntityName = className?uncap_first>

    public ${entityClass} create${className}() {
        return create${className}(0);
    }

    public ${entityClass} create${className}(int number) {
        ${entityClass} ${lowerEntityName} = new ${entityClass}();
        String uuidSeed = "${lowerEntityName}-" + number;
        ${lowerEntityName}.setId(UUID.nameUUIDFromBytes(uuidSeed.getBytes()));
<#list fields as field>
        ${lowerEntityName}.set${field.name?cap_first}(${field.defaultValue});
</#list>
        return ${lowerEntityName};
    }

    public List<${entityClass}> create${className}List(int size) {
        List<${entityClass}> ${lowerEntityName}s = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ${lowerEntityName}s.add(create${className}(i));
        }
        return ${lowerEntityName}s;
    }

<#if responseDTO?? && responseDTO.constructorParams?? && responseDTO.constructorParams?size gt 0>
    public List<${responseDTO.className}> create${className}ListResponse(int size) {
        List<${responseDTO.className}> ${lowerEntityName}s = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ${lowerEntityName}s.add(create${className}Response(i));
        }
        return ${lowerEntityName}s;
    }

</#if>
<#if requestDTO?? && requestDTO.constructorParams?? && requestDTO.constructorParams?size gt 0>
    public ${requestDTO.className} create${className}Request(int number) {
        return new ${requestDTO.className}(
<#list requestDTO.constructorParams as param>
            <#assign paramType = param.type>
            <#assign paramName = param.name>
            <#if paramType == "String">
                <#if paramName?lower_case?contains("email")>
            "${lowerEntityName}" + number + "@gmail.com"<#if param?has_next>,</#if>
                <#elseif paramName?lower_case?contains("password")>
            "password" + number<#if param?has_next>,</#if>
                <#else>
            "${paramName} " + number<#if param?has_next>,</#if>
                </#if>
            <#elseif paramType == "int" || paramType == "Integer">
            number<#if param?has_next>,</#if>
            <#elseif paramType == "long" || paramType == "Long">
            (long) number<#if param?has_next>,</#if>
            <#elseif paramType?contains("Role") || param.fullType?contains(".enums.") || param.fullType?contains(".enum.")>
            ${paramType}.USER<#if param?has_next>,</#if>
            <#elseif paramType == "LocalDate">
            LocalDate.now()<#if param?has_next>,</#if>
            <#elseif paramType == "LocalDateTime">
            LocalDateTime.now()<#if param?has_next>,</#if>
            <#else>
            null<#if param?has_next>,</#if>
            </#if>
</#list>
        );
    }

</#if>
<#if responseDTO?? && responseDTO.constructorParams?? && responseDTO.constructorParams?size gt 0>
    public ${responseDTO.className} create${className}Response(int number) {
        String uuidSeed = "${lowerEntityName}-" + number;
        UUID id = UUID.nameUUIDFromBytes(uuidSeed.getBytes());
        return new ${responseDTO.className}(
<#list responseDTO.constructorParams as param>
    <#assign paramType = param.type>
    <#assign paramName = param.name>
    <#if paramName == "id">
            id<#if param?has_next>,</#if>
    <#else>
        <#if paramType == "String">
            <#if paramName?lower_case?contains("email")>
            "${lowerEntityName}" + number + "@gmail.com"<#if param?has_next>,</#if>
            <#elseif paramName?lower_case?contains("password")>
            "password" + number<#if param?has_next>,</#if>
            <#else>
            "${paramName} " + number<#if param?has_next>,</#if>
            </#if>
        <#elseif paramType == "int" || paramType == "Integer">
            number<#if param?has_next>,</#if>
        <#elseif paramType == "long" || paramType == "Long">
            (long) number<#if param?has_next>,</#if>
        <#elseif paramType?contains("Role") || param.fullType?contains(".enums.") || param.fullType?contains(".enum.")>
            ${paramType}.USER<#if param?has_next>,</#if>
        <#elseif paramType == "LocalDate">
            LocalDate.now()<#if param?has_next>,</#if>
        <#elseif paramType == "LocalDateTime">
            LocalDateTime.now()<#if param?has_next>,</#if>
        <#else>
            null<#if param?has_next>,</#if>
        </#if>
    </#if>
</#list>
        );
    }
</#if>
}
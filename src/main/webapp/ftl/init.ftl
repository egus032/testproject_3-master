<script>
	var settings = {};
	<#list settings?keys as key>
		<#if settings[key]?is_number>
			settings.${key} = ${settings[key]};
		<#elseif settings[key]?is_boolean>
			settings.${key} = ${settings[key]?c};
		<#else>
			settings.${key} = '${settings[key]}';
		</#if>
	</#list>

	var user;
	<#if user??>
		user=${user.toJson()};
	</#if>
	var pageData;
	<#if pageData??>
		pageData=${pageData};
	</#if>
</script>
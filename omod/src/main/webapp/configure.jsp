<%@ include file="/WEB-INF/template/include.jsp"%>
<openmrs:require allPrivileges="Manage Global Properties" otherwise="/login.htm" redirect="/module/configure.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>
<%@ taglib prefix="springform" uri="http://www.springframework.org/tags/form"%>
<script type="text/javascript">
	var $j = jQuery.noConflict();
	
	function toggleSourceInfo(ele) {
		if($j(ele).is(":checked")) {
			$j("#sourceInfo").show();
		} else{
			$j("#sourceInfo").hide();
		}
	}
</script>
<style>
	.watermark {
		color:#999;
	}	
	fieldset {
		margin-bottom: 1em;
	}
	.indented {
		margin-left: 3em;
	}
	.even-configuration-item {
		padding: 0.5em; 0.3em;
	}
	.odd-configuration-item {
		padding: 0.5em; 0.3em;
		background-color: #e0e0e0;
	}
</style>

<h3>
	<spring:message code="metadatamapping.configure" />
</h3>

<springform:form modelAttribute="configureForm">
	
		<fieldset>
 		<legend><spring:message code="metadatamapping.configure.exporting"/></legend>
 		<div class="even-configuration-item">
			<springform:checkbox path="addLocalMappings" id="addLocalMappings" onchange="toggleSourceInfo(this)" />
			<b><label for="addLocalMappings"><spring:message code="metadatamapping.addLocalMappings" /></label></b> 
			<springform:errors path="addLocalMappings" cssClass="error"/>
			<div class="indented" id="sourceInfo" <c:if test="${!configureForm.addLocalMappings}">style="display: none"</c:if> >
				<spring:message code="metadatamapping.addLocalMappings.description"/>
				
				<p><spring:message code="metadatamapping.conceptSource.description.line1" /></p>
				<p>
					<spring:message code="metadatamapping.conceptSource.description.line2" /> <br />
					<springform:select path="conceptSourceUuid">
						<springform:option value="" />
						<springform:options items="${conceptSources}" itemLabel="name" itemValue="uuid" />
					</springform:select>
					<springform:errors path="conceptSourceUuid" cssClass="error"/> 
				</p> 
				<p>
					<spring:message code="metadatamapping.ifNot" />, 
					<a href="../../admin/maintenance/implementationid.form"><spring:message code="metadatamapping.configureImplementationId" /></a>
				</p>
			</div>
		</div>
	</fieldset>
	
	<p>
		<input type="submit" value="<spring:message code="general.save"/>" />
	</p>
</springform:form>

<%@ include file="/WEB-INF/template/footer.jsp"%>

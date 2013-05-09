<%
/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui"%>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />

<portlet:resourceURL var="weatherTimer" id="weatherTimer">
</portlet:resourceURL>

<body>

<table>
	<tr>
		<td>
			<img height="48px" width="48px" src="<%=request.getContextPath()%>/images/humidity.png"/>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
			<img height="48px" width="48px" src="<%=request.getContextPath()%>/images/wind.png"/>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td>
			<img height="44px" width="44px" src="<%=request.getContextPath()%>/images/temperature.png"/>
		</td>
	</tr>
	<tr>
		<td align="center">
			<label class="no_alarm" id="<portlet:namespace />humidity">---</label>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="center">
			<label class="no_alarm" id="<portlet:namespace />velocity">---</label>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="center">
			<label class="no_alarm" id="<portlet:namespace />temperature">---</label>
		</td>
	</tr>
	<tr>
		<td align="center">
			<label><liferay-ui:message key="label-humidity-unit"/></label>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="center">
			<label><liferay-ui:message key="label-velocity-unit"/></label>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<td align="center">
			<label><liferay-ui:message key="label-temperature-unit"/></label>
		</td>
	</tr>
</table>

</body>

<script>
<portlet:namespace />weatherTimer();

function <portlet:namespace />weatherTimer(){
	AUI().use('aui-io-request', function(A){

		var url = '<%=weatherTimer%>';
		A.io.request(url, {
			method : 'POST',
			data: {
			},
			dataType: 'json',
			on: {
				success: function() {    
					var message = this.get('responseData');
					if (message.success == true){
						if (message.humidityAlarm=="0"){
							document.getElementById("<portlet:namespace />humidity").className="no_alarm";
						}
						document.getElementById("<portlet:namespace />humidity").innerHTML=message.humidityValue;
						if (message.temperatureAlarm=="0"){
							document.getElementById("<portlet:namespace />temperature").className="no_alarm";
						}
						document.getElementById("<portlet:namespace />temperature").innerHTML=message.temperatureValue;
						if (message.windVelocityAlarm=="0"){
							document.getElementById("<portlet:namespace />velocity").className="no_alarm";
						}
						document.getElementById("<portlet:namespace />velocity").innerHTML=message.windVelocityValue;
					} else {
						
					}
				}
			}
		});
	});
}

setInterval(function(){ <portlet:namespace />weatherTimer()	},60000);
	
</script>
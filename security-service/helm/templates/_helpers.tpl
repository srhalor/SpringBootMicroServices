{{/*
Expand the name of the chart.
*/}}
{{- define "security-service.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default name for resources.
*/}}
{{- define "security-service.name" -}}
{{- .Chart.Name -}}
{{- end -}}


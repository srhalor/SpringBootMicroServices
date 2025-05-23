{{/*
Expand the name of the chart.
*/}}
{{- define "api-gateway.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default name for resources.
*/}}
{{- define "api-gateway.name" -}}
{{- .Chart.Name -}}
{{- end -}}


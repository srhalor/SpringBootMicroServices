{{/*
Expand the name of the chart.
*/}}
{{- define "spring-jpa-demo.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default name for resources.
*/}}
{{- define "spring-jpa-demo.name" -}}
{{- .Chart.Name -}}
{{- end -}}


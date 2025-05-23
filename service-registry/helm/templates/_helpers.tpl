{{/*
Expand the name of the chart.
*/}}
{{- define "service-registry.fullname" -}}
{{- printf "%s-%s" .Release.Name .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Create a default name for resources.
*/}}
{{- define "service-registry.name" -}}
{{- .Chart.Name -}}
{{- end -}}


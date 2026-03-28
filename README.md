# CRUD Project PB

[![CI](https://github.com/ManoelEgidio/Crud-Project-PB/actions/workflows/ci.yml/badge.svg)](https://github.com/ManoelEgidio/Crud-Project-PB/actions/workflows/ci.yml)
[![CodeQL](https://github.com/ManoelEgidio/Crud-Project-PB/actions/workflows/codeql.yml/badge.svg)](https://github.com/ManoelEgidio/Crud-Project-PB/actions/workflows/codeql.yml)
[![Delivery](https://github.com/ManoelEgidio/Crud-Project-PB/actions/workflows/delivery.yml/badge.svg)](https://github.com/ManoelEgidio/Crud-Project-PB/actions/workflows/delivery.yml)

Sistema CRUD integrado em Java com interface web, automacao de testes, cobertura com JaCoCo e pipeline de CI/CD pronto para deploy no Google Cloud Run.

## Workflows

- `CI`: build, testes e cobertura minima de 90 por cento
- `CodeQL`: analise estatica de seguranca
- `Delivery`: build da imagem, deploy em Cloud Run, validacao pos-deploy com Selenium e verificacao DAST

## Configuracao no GitHub

Variables:

- `GCP_PROJECT_ID`
- `GCP_REGION`
- `GCP_ARTIFACT_REPOSITORY`
- `CLOUD_RUN_SERVICE_PREFIX`
- `SPRING_PROFILE_DEV`
- `SPRING_PROFILE_TEST`
- `SPRING_PROFILE_PROD`

Secrets:

- `GCP_WORKLOAD_IDENTITY_PROVIDER`
- `GCP_SERVICE_ACCOUNT`

## Environments

Configure os environments abaixo no GitHub para separar os deploys:

- `development`
- `testing`
- `production`

Para `production`, adicione aprovacao manual com `required reviewers`.

rem We need an Entra/AD app first for necessary API access tokens
az ad app create --display-name "MyDayAdApp"
rem Create Service principal for generated app ID
az ad sp create --id APP-ID
rem Create credentials
az ad app credential reset --id APP-ID --append --years 5
rem Response will contain the relevant information app ID, password, tenant

rem Get Access token for Azure Management API
curl -X POST https://login.microsoftonline.com/TENANT-ID/oauth2/token -H "Content-Type: application/x-www-form-urlencoded" -d "grant_type=client_credentials&client_id=APP-ID&client_secret=APP-PASSWORD&resource=https%3A%2F%2Fmanagement.azure.com%2F"

rem Get subscription ID
az account list --output table

rem Add necessary role definition so we're allowed to call the API
az role definition create --role-definition @role-definition.json
rem Assign the role to our Service Principal
az role assignment create --assignee APP-ID --role "Custom Budget Manager" --scope /subscriptions/SUBSCRIPTION-ID

rem Create budget alert with limit of 5€/month
curl -X PUT https://management.azure.com/subscriptions/SUBSCRIPTION-ID/providers/Microsoft.Consumption/budgets/myday-cost-limit?api-version=2023-05-01 -H "Content-Type: application/json" -H "Authorization: Bearer ACCESS-TOKEN" --data-binary @cost-limit-5.json
rem Verify successful creation via https://portal.azure.com/#view/Microsoft_Azure_CostManagement/Menu/~/budgets/open/costalerts/openedBy/AzurePortal
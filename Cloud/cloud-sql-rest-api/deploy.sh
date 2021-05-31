GOOGLE_PROJECT_ID=capstone-project-fresco
CLOUD_RUN_SERVICE=cloud-sql-rest-api
INSTANCE_CONNECTION_NAME=capstone-project-fresco:asia-southeast2:frescodb
DB_USER=root
DB_PASS=cap0468
DB_NAME=user_data

gcloud builds submit --tag gcr.io/$GOOGLE_PROJECT_ID/$CLOUD_RUN_SERVICE \
  --project=$GOOGLE_PROJECT_ID

gcloud run deploy $CLOUD_RUN_SERVICE \
  --image gcr.io/$GOOGLE_PROJECT_ID/$CLOUD_RUN_SERVICE \
  --add-cloudsql-instances $INSTANCE_CONNECTION_NAME \
  --update-env-vars INSTANCE_CONNECTION_NAME=$INSTANCE_CONNECTION_NAME,DB_PASS=$DB_PASS,DB_USER=$DB_USER,DB_NAME=$DB_NAME \
  --platform managed \
  --region asia-southeast2 \
  --allow-unauthenticated \
  --project=$GOOGLE_PROJECT_ID

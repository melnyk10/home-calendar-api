# Home Calendar

[Home Calendar](https://omeln.atlassian.net/wiki/x/xIAD) is a smart, extensible event-tracking system designed to automatically monitor various event sources (
starting with esports) and notify users across different platforms.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

# Google Cloud Authentication Setup for Dev

This guide provides comprehensive instructions for setting up authentication with Google Cloud Platform using multiple methods.

## Prerequisites

Before starting, ensure you have:

- **macOS** with [Homebrew](https://brew.sh) installed
- **Google account** with appropriate permissions
- **Google Cloud Project** created and accessible
- **Project billing** enabled (if using paid services)

## Method 1: Google Cloud CLI Authentication

The Google Cloud CLI (`gcloud`) is the primary tool for managing GCP resources and authentication.

### Installation and Setup

1. **Install Google Cloud CLI**
   ```bash
   brew install --cask google-cloud-sdk
   ```

2. **Initialize the CLI** (recommended for first-time setup)
   ```bash
   gcloud init
   ```
   This command will:
   - Authenticate your Google account
   - Set your default project
   - Configure default compute region/zone

3. **Alternative: Manual authentication**
   ```bash
   gcloud auth login
   ```
   > This opens a browser window for Google account authentication

4. **Set your default project**
   ```bash
   gcloud config set project <YOUR_PROJECT_ID>
   ```

5. **Enable required APIs**
   ```bash
   gcloud services enable cloudresourcemanager.googleapis.com iam.googleapis.com --project=<YOUR_PROJECT_ID>
   ```

6. **Get an access token for API calls:**
   ```bash
   gcloud auth print-access-token
   ```

## Method 2: OAuth 2.0 Playground

The OAuth 2.0 Playground is useful for testing API calls and understanding OAuth flows.

### Setup Process

1. **Navigate to the playground**
   - Go to [OAuth 2.0 Playground](https://developers.google.com/oauthplayground)

2. **Configure OAuth credentials**
   - Click the **gear icon** (⚙️) in the top-right corner
   - Check **"Use your own OAuth credentials"**
   - Enter your `client_id` and `client_secret` from your GCP OAuth 2.0 credentials

3. **Select authorization scopes**
   - Choose appropriate scopes for your use case:
     ```
     https://www.googleapis.com/auth/userinfo.email
     https://www.googleapis.com/auth/userinfo.profile
     openid
     ```

4. **Complete the OAuth flow**
   - Click **"Authorize APIs"**
   - Grant permissions in the popup window
   - Exchange authorization code for tokens

5. **Use the access token**
   - Copy the generated access token
   - Include it in your HTTP requests as: `Authorization: Bearer <ACCESS_TOKEN>`


## Additional Resources

- [Google Cloud CLI Documentation](https://cloud.google.com/sdk/docs)
- [Google Cloud IAM Best Practices](https://cloud.google.com/iam/docs/using-iam-securely)
- [OAuth 2.0 for Web Server Applications](https://developers.google.com/identity/protocols/oauth2/web-server)
- [Mongock Migration](https://docs.quarkiverse.io/quarkus-mongock/dev/index.html)
- [Mongo Panache](https://quarkus.io/guides/mongodb-panache)
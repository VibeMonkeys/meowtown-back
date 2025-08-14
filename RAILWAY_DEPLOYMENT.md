# Railway Deployment Guide for Meowtown Backend

This guide will help you deploy the Meowtown Spring Boot backend application to Railway.

## Prerequisites

1. **Railway Account**: Sign up at [railway.app](https://railway.app)
2. **Railway CLI** (optional but recommended): Install via npm
   ```bash
   npm install -g @railway/cli
   ```
3. **Git Repository**: Your code should be in a Git repository

## Deployment Steps

### 1. Create Railway Project

#### Option A: Via Railway Dashboard
1. Go to [railway.app](https://railway.app)
2. Click "New Project"
3. Choose "Deploy from GitHub repo"
4. Select your repository
5. Railway will automatically detect the Dockerfile

#### Option B: Via CLI
```bash
# Login to Railway
railway login

# Initialize project in your backend directory
cd /path/to/meowtown-back
railway project create meowtown-backend

# Link to your repository
railway connect
```

### 2. Add Required Services

Railway provides managed PostgreSQL and Redis services. Add them to your project:

#### PostgreSQL Database
1. In Railway dashboard, click "New Service"
2. Choose "PostgreSQL"
3. Railway will provide these environment variables automatically:
   - `DATABASE_URL`
   - `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`

#### Redis Cache
1. In Railway dashboard, click "New Service"  
2. Choose "Redis"
3. Railway will provide these environment variables automatically:
   - `REDIS_URL`
   - `REDISHOST`, `REDISPORT`, `REDISPASSWORD`

### 3. Configure Environment Variables

In your Railway project dashboard, go to the backend service and add these environment variables:

#### Required Variables
```
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=your-super-secret-jwt-key-min-256-bits
AWS_ACCESS_KEY_ID=your-aws-access-key
AWS_SECRET_ACCESS_KEY=your-aws-secret-key
AWS_S3_BUCKET=your-s3-bucket-name
AWS_REGION=your-aws-region
CORS_ORIGIN=https://your-frontend-domain.com
```

#### Optional Variables (with defaults)
```
PORT=8080
DB_MAX_POOL_SIZE=20
DB_MIN_IDLE=5
SERVER_MAX_THREADS=200
SERVER_ACCEPT_COUNT=100
SSL_ENABLED=false
```

#### Email Configuration (if using email features)
```
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASS=your-email-password-or-app-password
```

### 4. Deploy

#### Automatic Deployment
Railway will automatically deploy when you push to your main branch.

#### Manual Deployment
```bash
# Using Railway CLI
railway deploy
```

### 5. Database Migration

If you need to run database migrations or initial setup:

1. **Database Schema**: The application is configured with `ddl-auto: validate` in production
2. **Initial Data**: You can run SQL scripts through Railway's database console
3. **Manual Setup**: Connect to your Railway PostgreSQL instance:
   ```bash
   railway connect postgres
   ```

### 6. Verify Deployment

1. Check your Railway dashboard for deployment status
2. Visit your application URL (provided by Railway)
3. Test health endpoint: `https://your-app-url.railway.app/actuator/health`
4. Check logs in Railway dashboard for any issues

## File Structure for Railway

The following files have been created/configured for Railway deployment:

```
meowtown-back/
├── Dockerfile                          # Multi-stage production build
├── railway.json                        # Railway deployment configuration
├── .railwayignore                     # Files to exclude from deployment
├── docker-compose.railway.yml         # Local testing with Railway-like setup
├── src/main/resources/
│   └── application-prod.yml           # Production configuration
└── RAILWAY_DEPLOYMENT.md             # This guide
```

## Important Configuration Details

### Database Configuration
- **Production**: Uses Railway's managed PostgreSQL
- **Connection**: Supports both `DATABASE_URL` and individual connection parameters
- **Pooling**: Configured with Hikari connection pooling for production

### Redis Configuration  
- **Production**: Uses Railway's managed Redis
- **SSL**: Configurable via `REDIS_SSL` environment variable
- **Timeout**: Set to 5 seconds for production stability

### Security
- **Non-root User**: Docker container runs as non-root user
- **Health Checks**: Built-in health checks for container orchestration
- **Error Handling**: Stack traces disabled in production
- **CORS**: Strict CORS policy in production

### Performance
- **JVM Options**: Optimized for container environments
- **Resource Limits**: Configured for Railway's resource constraints
- **Connection Pooling**: Optimized database connections
- **Graceful Shutdown**: Properly handles shutdown signals

## Troubleshooting

### Common Issues

1. **Build Fails**
   - Check Dockerfile syntax
   - Ensure all dependencies are available
   - Verify Java version compatibility (using JDK 21)

2. **Database Connection Issues**
   - Verify DATABASE_URL format: `postgresql://user:password@host:port/dbname`
   - Check if PostgreSQL service is running
   - Verify network connectivity between services

3. **Redis Connection Issues**
   - Check REDIS_HOST and REDIS_PORT variables
   - Verify Redis service is running
   - Test connection with redis-cli if available

4. **Environment Variable Issues**
   - Ensure all required variables are set
   - Check variable names match exactly (case-sensitive)
   - Verify JWT_SECRET is at least 256 bits

5. **Port Issues**
   - Railway automatically sets PORT environment variable
   - Ensure your application uses `server.port: ${PORT:8080}`

### Getting Help

- **Railway Logs**: Check deployment and runtime logs in Railway dashboard
- **Application Logs**: Monitor application logs for Spring Boot specific issues
- **Health Endpoint**: Use `/actuator/health` to check application status
- **Railway Discord**: Join Railway's Discord community for support

## Scaling and Production Considerations

1. **Database**: Consider upgrading PostgreSQL plan based on usage
2. **Redis**: Monitor Redis memory usage and adjust accordingly  
3. **Application**: Railway supports horizontal scaling
4. **CDN**: Consider using CloudFlare or similar for static assets
5. **Monitoring**: Set up external monitoring for production applications

## Cost Optimization

1. **Sleep Mode**: Railway can automatically sleep inactive applications
2. **Resource Limits**: Monitor and adjust resource allocations
3. **Database**: Use connection pooling to minimize database connections
4. **Caching**: Implement Redis caching to reduce database load

## Security Best Practices

1. **Environment Variables**: Never commit secrets to version control
2. **HTTPS**: Railway provides HTTPS by default
3. **CORS**: Configure strict CORS policies
4. **Database**: Use strong passwords and restrict access
5. **Updates**: Keep dependencies updated for security patches
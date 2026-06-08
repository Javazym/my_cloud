@Echo Off
Setlocal EnableDelayedExpansion

echo ========================================
echo Testing Agent API through Gateway
echo ========================================
echo.

REM Test 1: Get pending products
echo [Test 1] GET /agent/ai/products/pending?pageNum=1^&pageSize=10
echo -------------------------------------------
curl -v http://localhost:8889/agent/ai/products/pending?pageNum=1^&pageSize=10
echo.
echo.

echo [Test 2] GET /agent/health
echo -------------------------------------------
curl -v http://localhost:8889/agent/health
echo.

endlocal

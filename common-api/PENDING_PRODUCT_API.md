# 获取待审核商品接口说明

## ✅ 新增功能

在 `common-api` 模块中添加了获取待审核商品的远程调用接口。

---

## 📦 新增文件

### 1. ProductSimpleVO.java
**路径**: `common-api/src/main/java/org/example/common/dto/product/ProductSimpleVO.java`

**用途**: 简化版商品信息 VO，用于微服务间传递商品数据（审核列表场景）

**主要字段**:
- `id`: 商品ID
- `name`: 商品名称
- `image`: 主图
- `price`: 价格
- `merchantId`: 商家ID
- `merchantName`: 商家名称
- `auditStatus`: 审核状态（0-待审核，1-通过，2-驳回）
- `auditRemark`: 审核备注
- `createTime`: 创建时间

---

## 🔧 修改文件

### ShoppingFeignClient.java
**路径**: `common-api/src/main/java/org/example/common/client/ShoppingFeignClient.java`

**新增方法**:
```java
/**
 * 获取待审核商品列表
 *
 * @param pageNum 页码，从1开始
 * @param pageSize 每页数量
 * @return 待审核商品分页列表
 */
@GetMapping("/admin/products/pending")
ApiResult<Page<ProductSimpleVO>> getPendingProducts(
        @RequestParam("pageNum") int pageNum,
        @RequestParam("pageSize") int pageSize
);
```

**依赖更新**:
- 添加 `spring-data-commons` 依赖（支持 Page 类型）

---

## 📝 使用示例

### 在其他微服务中调用

```java
@Service
@RequiredArgsConstructor
public class AuditService {
    
    private final ShoppingFeignClient shoppingFeignClient;
    
    /**
     * 获取待审核商品列表
     */
    public void getPendingProducts() {
        // 调用 Feign 接口
        ApiResult<Page<ProductSimpleVO>> result = 
            shoppingFeignClient.getPendingProducts(1, 10);
        
        if (result != null && result.isSuccess()) {
            Page<ProductSimpleVO> page = result.getData();
            
            // 处理分页数据
            List<ProductSimpleVO> products = page.getContent();
            long totalElements = page.getTotalElements();
            int totalPages = page.getTotalPages();
            
            // 遍历商品
            for (ProductSimpleVO product : products) {
                System.out.println("商品ID: " + product.getId());
                System.out.println("商品名称: " + product.getName());
                System.out.println("审核状态: " + product.getAuditStatus());
            }
        }
    }
}
```

### 配合 auth-server 使用

```java
@Service
@RequiredArgsConstructor
public class AdminDashboardService {
    
    private final AuthFeignClient authFeignClient;
    private final ShoppingFeignClient shoppingFeignClient;
    
    /**
     * 管理员仪表盘数据
     */
    public DashboardVO getDashboardData(String adminUserId) {
        DashboardVO dashboard = new DashboardVO();
        
        // 1. 获取管理员信息
        ApiResult<UserVO> userResult = authFeignClient.getUserById(adminUserId);
        if (userResult != null && userResult.isSuccess()) {
            dashboard.setAdmin(userResult.getData());
        }
        
        // 2. 获取待审核商品数量
        ApiResult<Page<ProductSimpleVO>> productResult = 
            shoppingFeignClient.getPendingProducts(1, 1);
        if (productResult != null && productResult.isSuccess()) {
            long pendingCount = productResult.getData().getTotalElements();
            dashboard.setPendingProductCount(pendingCount);
        }
        
        return dashboard;
    }
}
```

---

## 🎯 服务端实现要求

在 `shopping-server` 中需要实现对应的 Controller 接口：

```java
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    
    private final ProductService productService;
    
    /**
     * 获取待审核商品列表
     */
    @GetMapping("/pending")
    public ApiResult<Page<ProductSimpleVO>> getPendingProducts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        // 查询待审核商品（auditStatus = 0）
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<Product> products = productService.findPendingProducts(pageable);
        
        // 转换为 ProductSimpleVO
        Page<ProductSimpleVO> voPage = products.map(this::convertToSimpleVO);
        
        return ApiResult.success(voPage);
    }
    
    private ProductSimpleVO convertToSimpleVO(Product product) {
        return ProductSimpleVO.builder()
                .id(product.getId())
                .name(product.getName())
                .subName(product.getSubName())
                .image(product.getImage())
                .price(product.getPrice())
                .originalPrice(product.getOriginalPrice())
                .stock(product.getStock())
                .soldCount(product.getSoldCount())
                .merchantId(product.getMerchant().getId())
                .merchantName(product.getMerchant().getName())
                .categoryId(product.getCategoryId())
                .publishStatus(product.getPublishStatus())
                .auditStatus(product.getAuditStatus() != null ? 
                    product.getAuditStatus().getCode() : 0)
                .auditRemark(product.getAuditRemark())
                .auditTime(product.getAuditTime())
                .createTime(product.getCreatedAt())
                .build();
    }
}
```

---

## 📊 数据结构

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | int | 是 | 页码，从1开始 |
| pageSize | int | 是 | 每页数量 |

### 响应结构
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "测试商品",
        "image": "http://example.com/image.jpg",
        "price": 99.99,
        "merchantId": 1,
        "merchantName": "测试店铺",
        "auditStatus": 0,
        "createTime": "2026-06-03T23:45:00"
      }
    ],
    "totalElements": 100,
    "totalPages": 10,
    "size": 10,
    "number": 0
  },
  "timestamp": "2026-06-03T23:49:00"
}
```

---

## ⚠️ 注意事项

1. **分页参数**: pageNum 从 1 开始，但服务端可能需要转换为从 0 开始
2. **性能优化**: 避免频繁调用，建议添加缓存
3. **异常处理**: Feign 调用可能失败，需要添加熔断降级
4. **超时配置**: 注意 Feign 的超时时间设置

---

## 🔗 相关接口

当前 `ShoppingFeignClient` 提供的接口：

1. ✅ `getMerchantByUserId()` - 根据用户ID获取商户信息
2. ✅ `getPendingProducts()` - 获取待审核商品列表（新增）

---

## 📅 更新记录

- **2026-06-03**: 初始版本，添加待审核商品查询接口
- **依赖**: spring-data-commons 已添加到 common-api

---

**状态**: ✅ 已完成  
**编译**: ✅ BUILD SUCCESS  
**可用**: ✅ 是（需要在 shopping-server 中实现对应接口）

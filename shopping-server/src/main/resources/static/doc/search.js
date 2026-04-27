let api = [];
const apiDocListSize = 1
api.push({
    name: 'default',
    order: '1',
    list: []
})
api[0].list.push({
    alias: 'CartController',
    order: '1',
    link: '购物车控制器',
    desc: '购物车控制器',
    list: []
})
api[0].list[0].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart',
    methodId: '9978a73d5a32fdfd2d87c832ab1baaaa',
    desc: '获取购物车列表',
});
api[0].list[0].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart/checked',
    methodId: '29fa576dbfe1eb044491d7ef22e10679',
    desc: '获取选中的购物车商品',
});
api[0].list[0].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart/count',
    methodId: '90f40cac0b2db3488e3f554faecbe5ad',
    desc: '获取购物车商品数量',
});
api[0].list[0].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart/statistics',
    methodId: 'c70c2ae2e16e2845b01d0c92e719b7a7',
    desc: '获取购物车统计信息',
});
api[0].list[0].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart',
    methodId: '05cfbf18a0d76c8601b3d8df575c9e78',
    desc: '添加商品到购物车',
});
api[0].list[0].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart/{cartItemId}/quantity',
    methodId: '137cd6e5ae22dbc384be2609b70232ec',
    desc: '更新购物车商品数量',
});
api[0].list[0].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart/{cartItemId}/check',
    methodId: 'eab30d32c50224d9ccc6139ed3ae553a',
    desc: '选中/取消选中购物车商品',
});
api[0].list[0].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart/check-all',
    methodId: '7c1e4a73a6eba8ac3abd0457d5aab9ca',
    desc: '全选/取消全选',
});
api[0].list[0].list.push({
    order: '9',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart/{cartItemId}',
    methodId: '84be55f534433071502bc55b1f4a0547',
    desc: '删除购物车商品',
});
api[0].list[0].list.push({
    order: '10',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart/batch',
    methodId: '3ad35db620857798a30a4233bb56025e',
    desc: '批量删除购物车商品',
});
api[0].list[0].list.push({
    order: '11',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/cart/clear',
    methodId: '200e300f082087a30ded786740e31f42',
    desc: '清空购物车',
});
api[0].list.push({
    alias: 'ReviewController',
    order: '2',
    link: '评价控制器',
    desc: '评价控制器',
    list: []
})
api[0].list[1].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/reviews/product/{productId}',
    methodId: 'a2e6db9c04231c18c7acab92b04a38cb',
    desc: '获取商品评价列表',
});
api[0].list[1].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/reviews/{reviewId}',
    methodId: '282e2adfab76d1b7ec4abb39efd8883d',
    desc: '获取评价详情',
});
api[0].list[1].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/reviews',
    methodId: 'fceb9ad8c92d3ec2fc8d77c9362641ad',
    desc: '添加评价',
});
api[0].list[1].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/reviews/{reviewId}/reply',
    methodId: 'b86a055504266443a9370e6be02ba8ea',
    desc: '商家回复评价',
});
api[0].list[1].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/reviews/{reviewId}/like',
    methodId: 'f9519e613165ae0f2614c57d49d5b3d9',
    desc: '点赞评价',
});
api[0].list[1].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/reviews/{reviewId}',
    methodId: '9bc2e67bd3cddb7e2a45deca12e47ecb',
    desc: '删除评价',
});
api[0].list[1].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/reviews/statistics/{productId}',
    methodId: 'af2f9bd8416d2ec4ffd609c1db4cec1a',
    desc: '获取评价统计',
});
api[0].list.push({
    alias: 'MerchantController',
    order: '3',
    link: '商家控制器',
    desc: '商家控制器',
    list: []
})
api[0].list[2].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/merchants/apply',
    methodId: '58710ce20ef61b4957e7f5e6866c5e62',
    desc: '申请入驻',
});
api[0].list[2].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/merchants',
    methodId: 'e42437966e46bd40afd0ab09735163c5',
    desc: '获取商家信息',
});
api[0].list[2].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/merchants/apply',
    methodId: '6e3cb2a55917e25b07513e296dbff443',
    desc: '获取商家入驻信息',
});
api[0].list[2].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/merchants/by-user',
    methodId: '1c412d2fd67e293b4554684c1844e31f',
    desc: '根据用户ID获取商家信息',
});
api[0].list[2].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/merchants/{merchantId}',
    methodId: '2a0c5259b9a3f6bf36144d6f05811793',
    desc: '更新商家信息',
});
api[0].list[2].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/merchants/{merchantId}/statistics',
    methodId: '290d997884918926524324903e4e0741',
    desc: '获取商家统计数据',
});
api[0].list.push({
    alias: 'AdminController',
    order: '4',
    link: '管理员控制器',
    desc: '管理员控制器',
    list: []
})
api[0].list[3].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/login',
    methodId: '2e37bc3be18642c0163859ec6ebeec0a',
    desc: '管理员登录',
});
api[0].list[3].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/current',
    methodId: '1a9f75a9f234587784b7a7a133984604',
    desc: '获取当前登录管理员',
});
api[0].list[3].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/password',
    methodId: '2fc5667c15e43df7c04c1b20deb51a2f',
    desc: '修改密码',
});
api[0].list[3].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/statistics',
    methodId: 'd39e3ed38a3548be72acd75af06298ae',
    desc: '获取平台统计数据',
});
api[0].list[3].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchant-applications/pending',
    methodId: '6c22dcb163c6b73ab046ee051d881c9d',
    desc: '获取待审核的商家申请列表',
});
api[0].list[3].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchant-applications/all',
    methodId: '866c4ac3d84df210d257fa1e7ee0fd6b',
    desc: '获取所有商家申请列表',
});
api[0].list[3].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchant-applications/audit',
    methodId: 'e18711ddc3ef9b6eb6afd70f21745e9a',
    desc: '审核商家入驻申请',
});
api[0].list[3].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchants/all',
    methodId: '0780b7ee7fbf31d97635bf6a886493a7',
    desc: '获取所有商家列表',
});
api[0].list[3].list.push({
    order: '9',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchants/by-status',
    methodId: 'f31325279eae0fa55db6ed86de610807',
    desc: '根据状态获取商家列表',
});
api[0].list[3].list.push({
    order: '10',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchants/by-audit-status',
    methodId: '5fdaadc0e25d1d1ac74ba03925e2eaf3',
    desc: '根据审核状态获取商家列表',
});
api[0].list[3].list.push({
    order: '11',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchants/{id}',
    methodId: 'ad786d635508c8e362317a8b231fe4b1',
    desc: '获取商家详情',
});
api[0].list[3].list.push({
    order: '12',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchants/{id}/status',
    methodId: '7d6e6e43b82884ffd33dad64d9ba4c64',
    desc: '更新商家状态',
});
api[0].list[3].list.push({
    order: '13',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchants/{id}',
    methodId: '920153d571aa121a288d4823ad80324d',
    desc: '删除商家',
});
api[0].list[3].list.push({
    order: '14',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchants/grouped',
    methodId: 'f099b8d60156f8de9a3d5faf0b7f79b1',
    desc: '按主营类目分组展示商家',
});
api[0].list[3].list.push({
    order: '15',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/admin/merchants/by-category',
    methodId: '0b97d49adc2a65648ecbaef84e5bcd0c',
    desc: '获取指定类目的商家列表',
});
api[0].list.push({
    alias: 'CouponController',
    order: '5',
    link: '优惠券控制器',
    desc: '优惠券控制器',
    list: []
})
api[0].list[4].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/coupons',
    methodId: 'fe5a692c7c3b87cfa9f3e3f2d6f8087e',
    desc: '获取优惠券列表',
});
api[0].list[4].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/coupons/available',
    methodId: '256bd6efac03fa9c98dfce4c7e09d663',
    desc: '获取可用优惠券',
});
api[0].list[4].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/coupons/{couponId}',
    methodId: 'f3671f062327cf61c426c747bc1091bc',
    desc: '获取优惠券详情',
});
api[0].list[4].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/coupons/{couponId}/receive',
    methodId: '74e56e84625329a9acd84bc7b47c54c2',
    desc: '领取优惠券',
});
api[0].list[4].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/coupons/my',
    methodId: '1110f9398928e47b2b81c7fb95c3e33d',
    desc: '获取用户优惠券',
});
api[0].list[4].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/coupons/validate',
    methodId: 'd09acbaf95451b812021ecabb0d12517',
    desc: '验证优惠券',
});
api[0].list[4].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/coupons/{couponId}/use',
    methodId: '310e9bfa76798ee11ef8dc640c482479',
    desc: '使用优惠券',
});
api[0].list.push({
    alias: 'FinanceController',
    order: '6',
    link: '财务控制器',
    desc: '财务控制器',
    list: []
})
api[0].list[5].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/finance/account/{merchantId}',
    methodId: 'd9ec720dd1f37b27767269f4594743ac',
    desc: '获取商家账户信息',
});
api[0].list[5].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/finance/data/{merchantId}',
    methodId: '8065eed639c9e758a15e956f5ab32e37',
    desc: '获取财务数据',
});
api[0].list[5].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/finance/withdraw/{merchantId}',
    methodId: 'c3895150e980b7b6b42b38ca5dcec61d',
    desc: '申请提现',
});
api[0].list[5].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/finance/withdraw/{merchantId}',
    methodId: '1a3f01155469d78dc1f3756f2d9c8adf',
    desc: '获取提现记录',
});
api[0].list.push({
    alias: 'FileController',
    order: '7',
    link: '',
    desc: '',
    list: []
})
api[0].list[6].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/file',
    methodId: '019b0977d779afdc0e9706c41c64de0a',
    desc: '上传文件',
});
api[0].list[6].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/file',
    methodId: '723ec2a969ea3bc44241d89630024d6d',
    desc: '删除文件',
});
api[0].list.push({
    alias: 'ProductController',
    order: '8',
    link: '商品控制器',
    desc: '商品控制器',
    list: []
})
api[0].list[7].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products',
    methodId: '1ee9892360a2eab15b22104e80e71114',
    desc: '分页查询商品',
});
api[0].list[7].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/{productId}',
    methodId: 'e460c4a2ec9c2f4cfd3133004217dd59',
    desc: '获取商品详情',
});
api[0].list[7].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/hot',
    methodId: '4de814caa3b1d0fc9cce3871ce26640f',
    desc: '获取热卖商品',
});
api[0].list[7].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/featured',
    methodId: 'c8ad2a84e0f12f7ec84bc4aa9519a299',
    desc: '获取精选商品',
});
api[0].list[7].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/new',
    methodId: 'fcd41e2318af213abb2f9eb90136f7a7',
    desc: '获取新品',
});
api[0].list[7].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/recommended',
    methodId: 'f6e53d3e1a2b3614344a576da77cca1e',
    desc: '获取推荐商品',
});
api[0].list[7].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/search',
    methodId: 'fc40c304b2271fabd2298b923bd366b4',
    desc: '搜索商品',
});
api[0].list[7].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/categories',
    methodId: 'e3cbd976c2d814d6a653ddec07b5b99d',
    desc: '获取商品分类列表',
});
api[0].list[7].list.push({
    order: '9',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/categories/{categoryId}',
    methodId: '76ddd1c6ae98af0f5e42c5465ba5a454',
    desc: '获取商品分类详情',
});
api[0].list[7].list.push({
    order: '10',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products',
    methodId: '89e4415d779ad0291fc5858d5b78c1a9',
    desc: '创建商品',
});
api[0].list[7].list.push({
    order: '11',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/{productId}',
    methodId: '8461eb82ca1c8b43fd4cfb7e7638728f',
    desc: '更新商品',
});
api[0].list[7].list.push({
    order: '12',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/products/{productId}',
    methodId: 'f566cc9d4b9ff0850c43186997090b49',
    desc: '删除商品',
});
api[0].list.push({
    alias: 'FavoriteController',
    order: '9',
    link: '收藏控制器',
    desc: '收藏控制器',
    list: []
})
api[0].list[8].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/favorites',
    methodId: 'e6963c62bcfe37ab248165c1933515af',
    desc: '获取用户收藏列表',
});
api[0].list[8].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/favorites',
    methodId: 'f39872dd87c57fff2617d469e7a17f4d',
    desc: '添加收藏',
});
api[0].list[8].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/favorites/{productId}',
    methodId: 'bb59bef4cd48b0fb5e5dee3ebb73533e',
    desc: '取消收藏',
});
api[0].list[8].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/favorites/check',
    methodId: '9eec192bdb88922a11199366ca6ff6d3',
    desc: '检查是否已收藏',
});
api[0].list[8].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/favorites/count',
    methodId: '07c5cd887379052063bf07d3b67b3d96',
    desc: '获取收藏数量',
});
api[0].list[8].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/favorites/clear',
    methodId: '5291bf067211305402a89cec1e977377',
    desc: '清空收藏夹',
});
api[0].list[8].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/favorites/batch',
    methodId: '5c8befb212a392f00dbe4f7dde86eb98',
    desc: '批量删除收藏',
});
api[0].list.push({
    alias: 'MarketingController',
    order: '10',
    link: '运营控制器',
    desc: '运营控制器',
    list: []
})
api[0].list[9].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/marketing/banners',
    methodId: '47b67504ec45e03ea42844bbe9394d02',
    desc: '获取轮播图列表',
});
api[0].list[9].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/marketing/announcements',
    methodId: '2d913017ec5fa514c414c0449204abfd',
    desc: '获取公告列表',
});
api[0].list[9].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/marketing/announcements/{announcementId}',
    methodId: '5d1d2cc20ecb8e090f852494fb8c8031',
    desc: '获取公告详情',
});
api[0].list[9].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/marketing/banners/{bannerId}/click',
    methodId: '6f50dd9566ee2b1c8e295daa8b448140',
    desc: '增加轮播图点击次数',
});
api[0].list.push({
    alias: 'UserController',
    order: '11',
    link: '用户控制器',
    desc: '用户控制器',
    list: []
})
api[0].list[10].list.push({
    order: '1',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/users/me',
    methodId: '658f884c826373ceafca16da34640b1a',
    desc: '获取当前用户信息',
});
api[0].list[10].list.push({
    order: '2',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/users',
    methodId: '7f25a4d121a5b14e62ea4c309330c305',
    desc: '获取用户详情',
});
api[0].list[10].list.push({
    order: '3',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/users',
    methodId: '81ae070367acd41f5c3531b938f2cf94',
    desc: '更新用户信息',
});
api[0].list[10].list.push({
    order: '4',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/users/addresses',
    methodId: '340c94c2bf4e978ae61e11549d06cc9e',
    desc: '获取用户收货地址列表',
});
api[0].list[10].list.push({
    order: '5',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/users/addresses/{addressId}',
    methodId: 'ac3730619308d35112c27069e6dfc83b',
    desc: '获取收货地址详情',
});
api[0].list[10].list.push({
    order: '6',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/users/addresses',
    methodId: 'b77b6aa0df0102f3f0c68bdb16704e49',
    desc: '添加收货地址',
});
api[0].list[10].list.push({
    order: '7',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/users/addresses/{addressId}',
    methodId: 'a1a6a1d6b48f03ebfead6d6183251fe6',
    desc: '更新收货地址',
});
api[0].list[10].list.push({
    order: '8',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/users/addresses/{addressId}',
    methodId: '603aaff83fc77da4080b0602a87f50d9',
    desc: '删除收货地址',
});
api[0].list[10].list.push({
    order: '9',
    deprecated: 'false',
    url: 'http://127.0.0.1:8901/users/addresses/{addressId}/default',
    methodId: '37beb00fa75b3b66a0ee230f3b319754',
    desc: '设置默认收货地址',
});
document.onkeydown = keyDownSearch;
function keyDownSearch(e) {
    const theEvent = e;
    const code = theEvent.keyCode || theEvent.which || theEvent.charCode;
    if (code === 13) {
        const search = document.getElementById('search');
        const searchValue = search.value.toLocaleLowerCase();

        let searchGroup = [];
        for (let i = 0; i < api.length; i++) {

            let apiGroup = api[i];

            let searchArr = [];
            for (let i = 0; i < apiGroup.list.length; i++) {
                let apiData = apiGroup.list[i];
                const desc = apiData.desc;
                if (desc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                    searchArr.push({
                        order: apiData.order,
                        desc: apiData.desc,
                        link: apiData.link,
                        alias: apiData.alias,
                        list: apiData.list
                    });
                } else {
                    let methodList = apiData.list || [];
                    let methodListTemp = [];
                    for (let j = 0; j < methodList.length; j++) {
                        const methodData = methodList[j];
                        const methodDesc = methodData.desc;
                        if (methodDesc.toLocaleLowerCase().indexOf(searchValue) > -1) {
                            methodListTemp.push(methodData);
                            break;
                        }
                    }
                    if (methodListTemp.length > 0) {
                        const data = {
                            order: apiData.order,
                            desc: apiData.desc,
                            link: apiData.link,
                            alias: apiData.alias,
                            list: methodListTemp
                        };
                        searchArr.push(data);
                    }
                }
            }
            if (apiGroup.name.toLocaleLowerCase().indexOf(searchValue) > -1) {
                searchGroup.push({
                    name: apiGroup.name,
                    order: apiGroup.order,
                    list: searchArr
                });
                continue;
            }
            if (searchArr.length === 0) {
                continue;
            }
            searchGroup.push({
                name: apiGroup.name,
                order: apiGroup.order,
                list: searchArr
            });
        }
        let html;
        if (searchValue === '') {
            const liClass = "";
            const display = "display: none";
            html = buildAccordion(api,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        } else {
            const liClass = "open";
            const display = "display: block";
            html = buildAccordion(searchGroup,liClass,display);
            document.getElementById('accordion').innerHTML = html;
        }
        const Accordion = function (el, multiple) {
            this.el = el || {};
            this.multiple = multiple || false;
            const links = this.el.find('.dd');
            links.on('click', {el: this.el, multiple: this.multiple}, this.dropdown);
        };
        Accordion.prototype.dropdown = function (e) {
            const $el = e.data.el;
            let $this = $(this), $next = $this.next();
            $next.slideToggle();
            $this.parent().toggleClass('open');
            if (!e.data.multiple) {
                $el.find('.submenu').not($next).slideUp("20").parent().removeClass('open');
            }
        };
        new Accordion($('#accordion'), false);
    }
}

function buildAccordion(apiGroups, liClass, display) {
    let html = "";
    if (apiGroups.length > 0) {
        if (apiDocListSize === 1) {
            let apiData = apiGroups[0].list;
            let order = apiGroups[0].order;
            for (let j = 0; j < apiData.length; j++) {
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#' + apiData[j].alias + '">' + apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                html += '<ul class="sectlevel2" style="'+display+'">';
                let doc = apiData[j].list;
                for (let m = 0; m < doc.length; m++) {
                    let spanString;
                    if (doc[m].deprecated === 'true') {
                        spanString='<span class="line-through">';
                    } else {
                        spanString='<span>';
                    }
                    html += '<li><a href="#' + doc[m].methodId + '">' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                }
                html += '</ul>';
                html += '</li>';
            }
        } else {
            for (let i = 0; i < apiGroups.length; i++) {
                let apiGroup = apiGroups[i];
                html += '<li class="'+liClass+'">';
                html += '<a class="dd" href="#_'+apiGroup.order+'_' + apiGroup.name + '">' + apiGroup.order + '.&nbsp;' + apiGroup.name + '</a>';
                html += '<ul class="sectlevel1">';

                let apiData = apiGroup.list;
                for (let j = 0; j < apiData.length; j++) {
                    html += '<li class="'+liClass+'">';
                    html += '<a class="dd" href="#' + apiData[j].alias + '">' +apiGroup.order+'.'+ apiData[j].order + '.&nbsp;' + apiData[j].desc + '</a>';
                    html += '<ul class="sectlevel2" style="'+display+'">';
                    let doc = apiData[j].list;
                    for (let m = 0; m < doc.length; m++) {
                       let spanString;
                       if (doc[m].deprecated === 'true') {
                           spanString='<span class="line-through">';
                       } else {
                           spanString='<span>';
                       }
                       html += '<li><a href="#' + doc[m].methodId + '">'+apiGroup.order+'.' + apiData[j].order + '.' + doc[m].order + '.&nbsp;' + spanString + doc[m].desc + '<span></a> </li>';
                   }
                    html += '</ul>';
                    html += '</li>';
                }

                html += '</ul>';
                html += '</li>';
            }
        }
    }
    return html;
}
# 工程说明

**此工程是基于ID混淆方案实现的隐匿查询,目的是为了实现隐匿查询的快速并且资源消耗小**

**此方案适用于混淆度要求不高的厂家(小于10000),并且ID范围区间是有限且便于构建的场景**

# 使用方式

查询方和数据方都运行此工程，其中数据需要设置在工程下面对应的目录，目前只支持文本数据。

查询方调用query接口，POST http://localhost:8081/pir/query
其中参数

`
{

    rate: 10 -->混淆度
    remote： 127.0.0.1：8080 -->数据方服务地址
    data： data.csv  -->查询的数据路径
    searchId: 12455 - ->任意ID唯一即可

}
`

**也可以直接调用后台，参考test目录下的代码即可，系统可以支持更多的ID格式**

# 实现原理

1. 查询方首先获取足够数量的公钥（大于等于混淆度+1），由数据方生成。

2. 查询方根据查询ID以及混淆度，生成相应数量的虚假ID

3. 查询方将真实ID随机插入到虚假ID中，然后将ID以及使用对应位置的公钥加密一个随机数E（R）发送给查询方

4. 数据方根据ID列表，查找对应的数据。并且将查询方发送的E（R）采用私钥依次按照顺序的私钥进行解密，获得对应的密钥列表。

5. 数据方将相应的数据与密钥列表进行按位异或加密，将数据发送给查询方

6. 查询方将收到的数据，与自己的随机数R进行异或操作，只有查询位置上的数据解密成功，此数据即为查询的数据。
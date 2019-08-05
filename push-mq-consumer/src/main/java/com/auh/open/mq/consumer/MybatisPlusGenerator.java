package com.auh.open.mq.consumer;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * 使用当前版本的代码生成器。数据库设计应该符合要求。
 */
public class MybatisPlusGenerator {

    public static void main(String[] args) {
        //使用人员常规使用需要关心的参数
        String[] tableNames = new String[]{"t_device"};
        String entityPackageName = "com.auh.open.mq.consumer.entity";
        String mapperPackageName = "com.auh.open.mq.consumer.mapper";
        boolean fileOverride = false;

        MybatisPlusGenerator mybatisPlusGenerator = new MybatisPlusGenerator();
        mybatisPlusGenerator.gen(tableNames, entityPackageName, mapperPackageName, fileOverride);
    }

    /**
     * 固有配置-根据项目约定
     */
    public void gen(String[] tableNames, String entityPackageName, String mapperPackageName, boolean fileOverride) {

        //配置数据源
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl("jdbc:mysql://172.1.1.1:3306/t_digg?characterEncoding=UTF-8");
        dataSourceConfig.setDriverName("com.mysql.jdbc.Driver");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("root");

        //配置全局
        GlobalConfig globalConfig = new GlobalConfig();
        String projectPath = System.getProperty("user.dir") + "/push-mq-consumer/";
        //最终生成位置=全局配置的outPutDir+包配置的packageName
        globalConfig.setOutputDir(projectPath + "/src/main/java");
        globalConfig.setFileOverride(fileOverride);
        System.out.println("fileOverride config：" + fileOverride);
        globalConfig.setOpen(false);
        globalConfig.setIdType(IdType.AUTO);

        //配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        //取消模板来限制不生成下面的类
        templateConfig.setController(null);
        templateConfig.setServiceImpl(null);
        templateConfig.setService(null);
        /*//取消生成xml
        templateConfig.setXml(null);*/

        //配置包
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(null);
        packageConfig.setEntity(entityPackageName);
        packageConfig.setMapper(mapperPackageName);
        packageConfig.setXml(mapperPackageName);

        //配置生成策略
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setTablePrefix("tbl_", "t_", "tb_");
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        //只能采用全局生成@TableFiled注解的形式。3.1.2版本的bug,字段自动添加@TableField注解的逻辑有问题。https://github.com/baomidou/mybatis-plus/issues/1393
        strategyConfig.setEntityTableFieldAnnotationEnable(true);

//        strategyConfig.setSuperEntityColumns("id");

        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setEntityBooleanColumnRemoveIsPrefix(true);
        strategyConfig.setInclude(tableNames);
        //当需要 例如：chouti_mobile 生成实体 Phone 、PhoneMapper的时候用于转化
      /*  strategyConfig.setNameConvert(new INameConvert() {

            @Override
            public String entityNameConvert(TableInfo tableInfo) {
                return null;
            }

            @Override
            public String propertyNameConvert(TableField field) {
                return null;
            }
        });*/

        ConfigBuilder config = new ConfigBuilder(packageConfig, dataSourceConfig, strategyConfig, templateConfig, globalConfig);
        //3.1.2版本的bug。必须设置一个对象。https://github.com/baomidou/mybatis-plus/issues/1392
        config.setInjectionConfig(new InjectionConfig() {
            @Override
            public void initMap() {

            }
        });

        //使得生成器不生成xml文件
        config.getPathInfo().remove(ConstVal.XML_PATH);
        String xmlPath = projectPath + "/src/main/java/" + "com/auh/open/mq/consumer/mapper/xml";
        config.getPathInfo().put(ConstVal.XML_PATH, xmlPath);

        //代码生成
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator.setConfig(config);
        autoGenerator.execute();

        System.out.println("生成完成");
    }


}

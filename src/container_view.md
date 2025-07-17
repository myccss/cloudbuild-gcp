根据提供的三个表结构，我将创建一个视图，整合 `container_image_deployment_history`、`container_image_scan_records` 和 `container_repo_mapping_info` 中的关键字段，以便在 dashboard 上展示容器镜像相关的详细信息。视图将包含 use_case、app_name、镜像名称、镜像版本、镜像仓库、环境、GKE 集群、namespace、扫描报告、扫描结果等字段。

### 分析
1. **关联字段**：
   - 三个表通过 `use_case` 和 `environment` 字段关联。
   - `container_image_deployment_history` 和 `container_image_scan_records` 还可以通过 `image_tag` 关联。
   - `app_name` 在 `container_image_deployment_history` 和 `container_repo_mapping_info` 中可以进一步加强关联。
2. **视图目标**：
   - 展示每个容器镜像的部署信息、扫描记录以及 GKE 集群和 namespace 的映射。
   - 包括 use_case、app_name、镜像名称、版本、仓库、环境、集群、namespace、扫描报告及结果。
3. **关联方式**：
   - 使用 `LEFT JOIN` 确保即使某些表中缺少记录也能显示部分信息（如扫描记录可能缺失）。
   - 假设 `image_tag` 在 `container_image_deployment_history` 和 `container_image_scan_records` 中一致，用于关联镜像版本。

### 创建视图
以下是视图的 SQL 定义：

```sql
CREATE VIEW container_dashboard_view AS
SELECT 
    dh.use_case,
    dh.app_name,
    dh.container_name AS image_name,
    dh.image_tag AS image_version,
    sr.image_registry AS nexus_repository,
    sr.image_path,
    dh.environment,
    rm.cluster_name,
    rm.namesapce AS namespace,
    sr.cont_scan_report,
    sr.foss_scan_report,
    sr.sast_scan_report,
    sr.sonar_scan_report,
    sr.cont_scan_result,
    sr.foss_scan_result,
    sr.sast_scan_result,
    sr.sonar_scan_result,
    dh.status AS deployment_status,
    dh.deploy_time,
    dh.trigger_user,
    dh.log_url
FROM 
    container_image_deployment_history dh
LEFT JOIN 
    container_image_scan_records sr 
    ON dh.use_case = sr.use_case 
    AND dh.environment = sr.environment 
    AND dh.image_tag = sr.image_tag
LEFT JOIN 
    container_repo_mapping_info rm 
    ON dh.use_case = rm.use_case 
    AND dh.app_name = rm.app_name 
    AND dh.environment = rm.environment;
```

### 视图字段说明
- **use_case**：来自 `container_image_deployment_history` 和其他表，用于标识业务用例。
- **app_name**：应用名称，来自 `container_image_deployment_history` 和 `container_repo_mapping_info`。
- **image_name**：容器名称 (`container_name`)，来自 `container_image_deployment_history`。
- **image_version**：镜像版本 (`image_tag`)，来自 `container_image_deployment_history`。
- **nexus_repository**：镜像仓库 (`image_registry`)，来自 `container_image_scan_records`。
- **image_path**：镜像路径，来自 `container_image_scan_records`。
- **environment**：环境（dev、sit、prod 等），来自 `container_image_deployment_history` 和其他表。
- **cluster_name**：GKE 集群名称，来自 `container_repo_mapping_info`。
- **namespace**：GKE 命名空间，来自 `container_repo_mapping_info`。
- **cont_scan_report**, **foss_scan_report**, **sast_scan_report**, **sonar_scan_report**：四项扫描报告，来自 `container_image_scan_records`。
- **cont_scan_result**, **foss_scan_result**, **sast_scan_result**, **sonar_scan_result**：扫描结果，来自 `container_image_scan_records`。
- **deployment_status**：部署状态，来自 `container_image_deployment_history`。
- **deploy_time**：部署时间，来自 `container_image_deployment_history`。
- **trigger_user**：触发部署的用户，来自 `container_image_deployment_history`。
- **log_url**：部署日志 URL，来自 `container_image_deployment_history`。

### 设计说明
1. **LEFT JOIN**：使用 LEFT JOIN 确保即使 `container_image_scan_records` 或 `container_repo_mapping_info` 中没有匹配记录，部署历史的信息仍能显示。
2. **关联条件**：
   - `use_case` 和 `environment` 用于跨表关联，确保视图数据按业务用例和环境对齐。
   - `image_tag` 用于关联部署历史和扫描记录的镜像版本。
   - `app_name` 加强 `container_image_deployment_history` 和 `container_repo_mapping_info` 的关联。
3. **字段选择**：包含 dashboard 所需的核心字段，忽略不必要的字段（如 `rossn_template_id`、`dependencies` 等）以保持简洁。
4. **灵活性**：视图支持查询所有环境（dev、sit、prod 等）的数据，适合 dashboard 展示。

### 查询视图
在 dashboard 中，可以直接查询视图获取数据，例如：
```sql
SELECT * FROM container_dashboard_view
WHERE environment = 'prod' AND use_case = 'example_use_case';
```

### 注意事项
1. **数据完整性**：如果 `image_tag` 在不同表中的格式不一致（例如大小写或前后缀差异），可能导致关联失败。需要确保数据一致性。
2. **性能**：视图基于三个表的 JOIN 操作，大数据量下可能需要索引支持（如在 `use_case`、`environment`、`image_tag`、`app_name` 上创建索引）。
3. **空值处理**：由于使用 LEFT JOIN，某些字段（如扫描报告或集群信息）可能为 NULL，dashboard 需处理空值展示。
4. **扩展性**：若需添加更多字段（如 `workload_name` 或 `git_url`），可修改视图定义。

### 优化建议
- **索引**：在以下字段上创建索引以提高查询性能：
  ```sql
  CREATE INDEX idx_deployment_history ON container_image_deployment_history (use_case, environment, image_tag);
  CREATE INDEX idx_scan_records ON container_image_scan_records (use_case, environment, image_tag);
  CREATE INDEX idx_repo_mapping ON container_repo_mapping_info (use_case, app_name, environment);
  ```
- **物化视图**：若 dashboard 查询频繁且数据更新不频繁，可考虑将视图改为物化视图以提升性能：
  ```sql
  CREATE MATERIALIZED VIEW container_dashboard_mat_view AS
  -- 视图定义同上
  WITH DATA;
  ```
  定期刷新：
  ```sql
  REFRESH MATERIALIZED VIEW container_dashboard_mat_view;
  ```

如需进一步调整视图结构、添加过滤条件或优化查询，请提供更多细节！

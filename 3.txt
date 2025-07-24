import requests
from collections import defaultdict
import time

def get_all_teams(org_name, token, prefix):
    """
    获取 GitHub 组织下所有 team，并筛选出以指定前缀开头的 team 的去重成员。
    
    Args:
        org_name (str): 组织名称
        token (str): GitHub Personal Access Token
        prefix (str): Team 名称前缀
    
    Returns:
        set: 去重后的成员登录名集合
    """
    # GitHub API 的基础 URL
    base_url = "https://api.github.com"
    headers = {
        "Authorization": f"token {token}",
        "Accept": "application/vnd.github.v3+json"
    }
    
    # 用于存储所有成员的集合（去重）
    all_members = set()
    
    # 获取所有 team，分页处理
    teams_url = f"{base_url}/orgs/{org_name}/teams"
    page = 1
    per_page = 100  # 每页最大 100 条

    while True:
        try:
            # 请求 team 列表
            response = requests.get(teams_url, headers=headers, params={"per_page": per_page, "page": page})
            
            # 检查响应状态
            if response.status_code == 403:
                # 处理 API 速率限制
                reset_time = int(response.headers.get("X-RateLimit-Reset", 0))
                wait_time = max(reset_time - int(time.time()), 0) + 5
                print(f"Rate limit exceeded. Waiting for {wait_time} seconds...")
                time.sleep(wait_time)
                continue
            elif response.status_code != 200:
                print(f"Error fetching teams: {response.status_code} - {response.json().get('message')}")
                break

            teams = response.json()
            if not teams:  # 如果没有更多 team，退出循环
                break

            # 处理每个 team
            for team in teams:
                team_name = team["name"]
                if team_name.startswith(prefix):
                    print(f"Processing team: {team_name}")
                    # 获取 team 成员
                    members = get_team_members(team["slug"], org_name, token)
                    all_members.update(members)

            # 翻页
            page += 1

        except Exception as e:
            print(f"Error processing teams: {e}")
            break

    return all_members

def get_team_members(team_slug, org_name, token):
    """
    获取指定 team 的成员列表。
    
    Args:
        team_slug (str): Team 的 slug
        org_name (str): 组织名称
        token (str): GitHub Personal Access Token
    
    Returns:
        set: 成员登录名集合
    """
    base_url = "https://api.github.com"
    headers = {
        "Authorization": f"token {token}",
        "Accept": "application/vnd.github.v3+json"
    }
    
    members = set()
    members_url = f"{base_url}/orgs/{org_name}/teams/{team_slug}/members"
    page = 1
    per_page = 100

    while True:
        try:
            response = requests.get(members_url, headers=headers, params={"per_page": per_page, "page": page})
            
            if response.status_code == 403:
                reset_time = int(response.headers.get("X-RateLimit-Reset", 0))
                wait_time = max(reset_time - int(time.time()), 0) + 5
                print(f"Rate limit exceeded for team {team_slug}. Waiting for {wait_time} seconds...")
                time.sleep(wait_time)
                continue
            elif response.status_code != 200:
                print(f"Error fetching members for team {team_slug}: {response.status_code} - {response.json().get('message')}")
                break

            members_data = response.json()
            if not members_data:
                break

            # 提取成员登录名
            for member in members_data:
                members.add(member["login"])

            page += 1

        except Exception as e:
            print(f"Error processing members for team {team_slug}: {e}")
            break

    return members

# 使用示例
if __name__ == "__main__":
    # 配置参数
    ORG_NAME = "your-organization"  # 替换为你的组织名称
    TOKEN = "your-personal-access-token"  # 替换为你的 GitHub Personal Access Token
    PREFIX = "dev-"  # 替换为你需要的前缀

    # 获取去重后的成员
    unique_members = get_all_teams(ORG_NAME, TOKEN, PREFIX)
    
    # 输出结果
    print(f"Unique members in teams with prefix '{PREFIX}':")
    for member in sorted(unique_members):
        print(member)
    print(f"Total unique members: {len(unique_members)}")

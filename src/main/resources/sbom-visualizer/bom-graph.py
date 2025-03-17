#
# bom.json 파일을 기반으로 한 SBOM 의존성 구조를 시각화하는 파이선
#
import json
import networkx as nx
import matplotlib.pyplot as plt

# 파일 경로
file_path = "./bom.json"

# JSON 파일 읽기
with open(file_path, "r", encoding="utf-8") as file:
    bom_data = json.load(file)

# JSON 데이터의 기본 구조 확인
bom_data.keys()

# 주요 데이터 추출
components = {comp["bom-ref"]: comp["name"] for comp in bom_data.get("components", [])}
dependencies = bom_data.get("dependencies", [])

# 의존성 데이터 샘플 확인
dependencies[:5]  # 처음 5개만 확인

# 의존성 그래프 생성
G = nx.DiGraph()

# 노드 추가 (모든 컴포넌트)
for ref, name in components.items():
    G.add_node(ref, label=name)

# 엣지 추가 (의존성 관계)
for dep in dependencies:
    ref = dep["ref"]
    for dependent in dep["dependsOn"]:
        G.add_edge(ref, dependent)

# 그래프 시각화
plt.figure(figsize=(12, 8))
pos = nx.spring_layout(G, k=0.5)  # 레이아웃 조정
nx.draw(G, pos, with_labels=True, node_size=100, font_size=8, edge_color="gray", alpha=0.6)
plt.title("SBOM Dependency Graph")
plt.show()

from __future__ import annotations

from collections import defaultdict
from typing import Any


class StoreState:
    def __init__(self):
        self.data: dict[str, list[dict[str, Any]]] = defaultdict(list)
        self.counters: dict[str, int] = defaultdict(int)
        self.tokens: dict[str, dict[str, Any]] = {}
        self.policy: dict[str, Any] = {
            "id": 1,
            "enabled": 0,
            "lowConfidenceThreshold": 0.75,
            "failureStrategy": "exception-pool",
            "promptTemplate": "你是考试批改与学情分析助手，请按要求输出 JSON。",
        }

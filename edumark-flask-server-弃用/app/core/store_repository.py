from __future__ import annotations

from copy import deepcopy
from datetime import datetime
from typing import Any

from app.core.store_seed import build_seed_data
from app.core.store_state import StoreState


class MemoryStore(StoreState):
    def bootstrap(self):
        if self.data["users"]:
            return

        for key, items in build_seed_data(self.now()).items():
            self.data[key] = items
        self._sync_counters()

    def _sync_counters(self):
        for key, items in self.data.items():
            self.counters[key] = max([item.get("id", 0) for item in items] + [0])

    def now(self) -> str:
        return datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    def next_id(self, key: str) -> int:
        self.counters[key] += 1
        return self.counters[key]

    def list(self, key: str) -> list[dict[str, Any]]:
        return self.data[key]

    def page(self, key: str, params: dict[str, Any], fuzzy_fields: list[str] | None = None) -> tuple[list[dict], int, int, int]:
        fuzzy_fields = fuzzy_fields or []
        page_num = int(params.get("pageNum", 1) or 1)
        page_size = int(params.get("pageSize", 10) or 10)
        items = deepcopy(self.data[key])

        for field, value in params.items():
            if value in (None, "", [], {}):
                continue
            if field in ("pageNum", "pageSize"):
                continue
            if field in fuzzy_fields:
                items = [item for item in items if value.lower() in str(item.get(field, "")).lower()]
            else:
                items = [item for item in items if str(item.get(field, "")) == str(value)]

        total = len(items)
        start = (page_num - 1) * page_size
        end = start + page_size
        return items[start:end], total, page_num, page_size

    def create(self, key: str, data: dict[str, Any]) -> dict[str, Any]:
        item = deepcopy(data)
        item["id"] = item.get("id") or self.next_id(key)
        item.setdefault("status", 1)
        item.setdefault("createTime", self.now())
        item.setdefault("updateTime", self.now())
        self.data[key].append(item)
        return item

    def update(self, key: str, data: dict[str, Any]) -> dict[str, Any] | None:
        item = self.find_by_id(key, data.get("id"))
        if not item:
            return None
        item.update({k: v for k, v in data.items() if v is not None})
        item["updateTime"] = self.now()
        return item

    def delete(self, key: str, item_id: int | str) -> bool:
        original = len(self.data[key])
        self.data[key] = [item for item in self.data[key] if str(item.get("id")) != str(item_id)]
        return len(self.data[key]) != original

    def batch_delete(self, key: str, ids: list[int | str]):
        id_set = {str(item_id) for item_id in ids}
        self.data[key] = [item for item in self.data[key] if str(item.get("id")) not in id_set]

    def find_by_id(self, key: str, item_id: int | str | None) -> dict[str, Any] | None:
        for item in self.data[key]:
            if str(item.get("id")) == str(item_id):
                return item
        return None

    def find_one(self, key: str, **kwargs) -> dict[str, Any] | None:
        for item in self.data[key]:
            if all(str(item.get(k)) == str(v) for k, v in kwargs.items()):
                return item
        return None

    def list_by(self, key: str, **kwargs) -> list[dict[str, Any]]:
        result = []
        for item in self.data[key]:
            if all(str(item.get(k)) == str(v) for k, v in kwargs.items()):
                result.append(deepcopy(item))
        return result

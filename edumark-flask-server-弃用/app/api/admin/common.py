from __future__ import annotations

from collections import defaultdict

from flask import Blueprint

from app.api.admin.auth_helpers import *
from app.api.admin.crud import *
from app.api.admin.serializers import *


admin_bp = Blueprint("admin", __name__)


__all__ = [name for name in globals() if not name.startswith("__")]

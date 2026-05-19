from __future__ import annotations

from collections import defaultdict

from flask import Blueprint

from app.api.app_client.access import *
from app.api.app_client.auth_helpers import *
from app.api.app_client.serializers import *


app_bp = Blueprint("app_client", __name__)


__all__ = [name for name in globals() if not name.startswith("__")]

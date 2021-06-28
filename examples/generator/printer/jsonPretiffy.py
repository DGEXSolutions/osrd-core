class JsonPretiffy:

    def pretiffy(json, depthIndent = "", indent = ""):
        if isinstance(json, bool):
            return indent + ("true" if json else "false")

        elif isinstance(json, int):
            return indent + str(json)

        elif isinstance(json, float):
            return indent + str(json)

        elif isinstance(json, str):
            return indent + '"' + json + '"'

        elif isinstance(json, list):
            if json == []:
                return indent + "[]"
            ans = indent + "[\n"
            for i, e in enumerate(json):
                ans += JsonPretiffy.pretiffy(e, depthIndent + "  ", depthIndent + "  ")
                if i != len(json) - 1:
                    ans += ",\n"
                else:
                    ans += "\n" + depthIndent + "]"
            return ans

        elif isinstance(json, dict):
            if json == {}:
                return indent + "{}"
            ans = indent + "{\n"
            for i, e in enumerate(json.items()):
                k, v = e
                ans += depthIndent + '  "' + k + '": ' + JsonPretiffy.pretiffy(v, depthIndent + "  ", "")
                if i != len(json) - 1:
                    ans += ",\n"
                else:
                    ans += "\n" + depthIndent + "}"
            return ans
        else:
            raise f"JSON Error"

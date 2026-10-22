"""Sorts and groups the imports of Java files (java, javax, org, com, net, others), like the upstream sources."""
import re
import sys

ORDER = ['java.', 'javax.', 'org.', 'com.', 'net.']


def group(imp):
    name = imp.split()[-1]
    for i, prefix in enumerate(ORDER):
        if name.startswith(prefix):
            return i
    return len(ORDER)


def sort_file(path):
    src = open(path, encoding='utf-8').read()
    lines = src.split('\n')
    idx = [i for i, l in enumerate(lines) if re.match(r'^import (static )?[\w.*]+;\s*$', l)]
    if not idx:
        return
    first, last = idx[0], idx[-1]
    if any(lines[i].strip() and i not in idx for i in range(first, last + 1)):
        raise SystemExit('%s: non-import lines between imports' % path)
    imports = sorted(set(lines[i].strip() for i in idx), key=lambda s: (group(s), s.split()[-1].rstrip(';')))
    out, prev = [], None
    for imp in imports:
        g = group(imp)
        if prev is not None and g != prev:
            out.append('')
        out.append(imp)
        prev = g
    lines[first:last + 1] = out
    open(path, 'w', encoding='utf-8', newline='\n').write('\n'.join(lines))


if __name__ == '__main__':
    for p in sys.argv[1:]:
        sort_file(p)

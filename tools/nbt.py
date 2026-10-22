"""Minimal NBT (Named Binary Tag) reader/writer for the asset tools (gzip-compressed structure files).

Tags are returned as (type_id, value) pairs so that a file can be written back exactly as it was read:
compounds are dicts of name -> (type, value), lists are (element_type, [values]).
"""
import gzip
import struct

END, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTE_ARRAY, STRING, LIST, COMPOUND, INT_ARRAY, LONG_ARRAY = range(13)


class _Reader:
    def __init__(self, data):
        self.data = data
        self.pos = 0

    def take(self, n):
        chunk = self.data[self.pos:self.pos + n]
        self.pos += n
        return chunk

    def unpack(self, fmt):
        size = struct.calcsize(fmt)
        return struct.unpack(fmt, self.take(size))[0]

    def string(self):
        return self.take(self.unpack('>H')).decode('utf-8')

    def payload(self, tag):
        if tag == BYTE: return self.unpack('>b')
        if tag == SHORT: return self.unpack('>h')
        if tag == INT: return self.unpack('>i')
        if tag == LONG: return self.unpack('>q')
        if tag == FLOAT: return self.unpack('>f')
        if tag == DOUBLE: return self.unpack('>d')
        if tag == BYTE_ARRAY: return list(struct.unpack('>%db' % (n := self.unpack('>i')), self.take(n)))
        if tag == STRING: return self.string()
        if tag == LIST:
            element = self.unpack('>b')
            return (element, [self.payload(element) for _ in range(self.unpack('>i'))])
        if tag == COMPOUND:
            out = {}
            while True:
                t = self.unpack('>b')
                if t == END:
                    return out
                name = self.string()
                out[name] = (t, self.payload(t))
        if tag == INT_ARRAY: return list(struct.unpack('>%di' % (n := self.unpack('>i')), self.take(4 * n)))
        if tag == LONG_ARRAY: return list(struct.unpack('>%dq' % (n := self.unpack('>i')), self.take(8 * n)))
        raise ValueError('unknown tag type %d' % tag)


def _write(out, tag, value):
    if tag == BYTE: out.append(struct.pack('>b', value))
    elif tag == SHORT: out.append(struct.pack('>h', value))
    elif tag == INT: out.append(struct.pack('>i', value))
    elif tag == LONG: out.append(struct.pack('>q', value))
    elif tag == FLOAT: out.append(struct.pack('>f', value))
    elif tag == DOUBLE: out.append(struct.pack('>d', value))
    elif tag == BYTE_ARRAY: out.append(struct.pack('>i%db' % len(value), len(value), *value))
    elif tag == STRING:
        raw = value.encode('utf-8')
        out.append(struct.pack('>H', len(raw)) + raw)
    elif tag == LIST:
        element, items = value
        out.append(struct.pack('>bi', element if items else END if element == END else element, len(items)))
        for item in items:
            _write(out, element, item)
    elif tag == COMPOUND:
        for name, (t, v) in value.items():
            out.append(struct.pack('>b', t))
            _write(out, STRING, name)
            _write(out, t, v)
        out.append(struct.pack('>b', END))
    elif tag == INT_ARRAY: out.append(struct.pack('>i%di' % len(value), len(value), *value))
    elif tag == LONG_ARRAY: out.append(struct.pack('>i%dq' % len(value), len(value), *value))
    else:
        raise ValueError('unknown tag type %d' % tag)


def load(path):
    """Returns (root_name, root_compound)"""
    with open(path, 'rb') as f:
        data = f.read()
    if data[:2] == b'\x1f\x8b':
        data = gzip.decompress(data)
    reader = _Reader(data)
    if reader.unpack('>b') != COMPOUND:
        raise ValueError('root tag is not a compound: ' + path)
    return reader.string(), reader.payload(COMPOUND)


def save(path, root_name, root):
    out = [struct.pack('>b', COMPOUND)]
    _write(out, STRING, root_name)
    _write(out, COMPOUND, root)
    with open(path, 'wb') as f:
        f.write(gzip.compress(b''.join(out)))

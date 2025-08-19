export const generateUUID = () => {
    const s: string[] = [];
    const hexDigits = '0123456789abcdef';
    for (let i = 0; i <= 36; i += 1) {
        const r = Math.round(Math.random() * 0x10);
        const substr = hexDigits.substring(r, r + 1);
        s.push(substr);
    }
    s[14] = '4';
    s[8] = '-';
    s[13] = '-';
    s[18] = '-';
    s[23] = '-';
    return s.join('');
};

export const isNull = (obj: any | null | undefined) =>
    obj === null || obj === undefined;

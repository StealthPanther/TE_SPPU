from collections import OrderedDict

def macro_pass1():
    pntab = OrderedDict()
    mdtp = 1
    kpdtp = 0
    paramNo = 1
    pp = 0
    kp = 0
    flag = 0
    Macroname = None

    with open("macro_input.asm", "r") as br, \
         open("mnt.txt", "w") as mnt, \
         open("mdt.txt", "w") as mdt, \
         open("kpdt.txt", "w") as kpdt, \
         open("pntab.txt", "w") as pnt, \
         open("intermediate.txt", "w") as ir:

        lines = br.readlines()
        i = 0
        while i < len(lines):
            line = lines[i].strip()
            if not line:
                i += 1
                continue

            parts = line.split()
            if parts[0].upper() == "MACRO":
                flag = 1
                i += 1
                line = lines[i].strip()
                parts = line.split()
                Macroname = parts[0]

                pp = 0
                kp = 0
                paramNo = 1
                pntab.clear()

                if len(parts) <= 1:
                    # no parameters
                    mnt.write(f"{parts[0]}\t{pp}\t{kp}\t{mdtp}\t{kpdtp if kp==0 else kpdtp+1}\n")
                    i += 1
                    continue

                for param in parts[1:]:
                    param = param.replace('&', '').replace(',', '')
                    if '=' in param:
                        kp += 1
                        keywordParam = param.split('=')
                        pntab[keywordParam[0]] = paramNo
                        paramNo += 1
                        if len(keywordParam) == 2:
                            kpdt.write(f"{keywordParam[0]}\t{keywordParam[1]}\n")
                        else:
                            kpdt.write(f"{keywordParam[0]}\t-\n")
                    else:
                        pntab[param] = paramNo
                        paramNo += 1
                        pp += 1

                mnt.write(f"{parts[0]}\t{pp}\t{kp}\t{mdtp}\t{kpdtp if kp==0 else kpdtp+1}\n")
                kpdtp += kp
                i += 1

            elif parts[0].upper() == "MEND":
                mdt.write(line + "\n")
                flag = 0
                kp = 0
                pp = 0
                mdtp += 1
                paramNo = 1

                pnt.write(f"{Macroname}:\t")
                for key in pntab.keys():
                    pnt.write(f"{key}\t")
                pnt.write("\n")
                pntab.clear()
                i += 1

            elif flag == 1:
                # inside macro definition
                for part in parts:
                    if '&' in part:
                        param = part.replace('&', '').replace(',', '')
                        pnt_index = pntab.get(param)
                        mdt.write(f"(P,{pnt_index})\t")
                    else:
                        mdt.write(part + "\t")
                mdt.write("\n")
                mdtp += 1
                i += 1

            else:
                # normal line, outside macro definition
                ir.write(line + "\n")
                i += 1

    print("Macro Pass1 Processing done. :)")

if __name__ == "__main__":
    macro_pass1()

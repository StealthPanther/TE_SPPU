def macro_pass2():
    # Read the Macro Name Table
    mnt = {}
    with open("mnt.txt", "r") as f:
        for line in f:
            parts = line.strip().split('\t')
            if len(parts) == 5:
                macro_name = parts[0]
                pp = int(parts[1])
                kp = int(parts[2])
                mdtp = int(parts[3])
                kpdtp_index = int(parts[4])
                mnt[macro_name] = {
                    'pp': pp,
                    'kp': kp,
                    'mdtp': mdtp,
                    'kpdtp': kpdtp_index
                }

    # Read the Macro Definition Table
    with open("mdt.txt", "r") as f:
        mdt = [line.strip() for line in f.readlines()]

    # Read the Keyword Parameter Default Table
    with open("kpdt.txt", "r") as f:
        kpdt = [line.strip() for line in f.readlines()]

    # Read intermediate and write expanded output
    with open("intermediate.txt", "r") as ir, open("expanded_output.asm", "w") as out:
        for line in ir:
            line = line.strip()
            if not line:
                continue

            parts = line.split()
            macro_call = parts[0]

            if macro_call in mnt:
                macro_info = mnt[macro_call]
                pp = macro_info['pp']
                kp = macro_info['kp']
                mdtp = macro_info['mdtp'] - 1  # MDT is 1-based indexed
                kpdtp = macro_info['kpdtp']

                actual_params = parts[1:] if len(parts) > 1 else []
                arg_tab = {}

                # Process positional parameters
                for i in range(pp):
                    if i < len(actual_params):
                        arg_tab[i+1] = actual_params[i].replace(',', '')
                    else:
                        arg_tab[i+1] = ''

                # Process keyword parameters with defaults from KPDT, with bounds checking
                keyword_params = {}
                for i in range(kp):
                    idx = kpdtp + i
                    if idx < len(kpdt):
                        kp_line = kpdt[idx]
                        parts_kpdt = kp_line.split('\t')
                        if len(parts_kpdt) == 2:
                            kname, kvalue = parts_kpdt
                            keyword_params[kname] = kvalue
                        # else: skip malformed kpdt lines
                    else:
                        # out-of-range: skip this keyword parameter
                        continue

                # Override defaults with actual provided keyword parameters
                for param in actual_params[pp:]:
                    if '=' in param:
                        k, v = param.split('=')
                        keyword_params[k.replace(',', '')] = v.replace(',', '')

                # Merge keyword params to arg_tab after positional ones
                for idx, (k, v) in enumerate(keyword_params.items()):
                    arg_tab[pp + idx + 1] = v

                # Expand macro definition
                i = mdtp
                while i < len(mdt):
                    mdt_line = mdt[i]
                    if mdt_line.upper() == "MEND":
                        break
                    expanded_line = []
                    for token in mdt_line.split():
                        if token.startswith("(P,"):
                            idx_str = token[3:-1]
                            try:
                                idx = int(idx_str)
                                expanded_line.append(arg_tab.get(idx, token))
                            except ValueError:
                                expanded_line.append(token)
                        else:
                            expanded_line.append(token)
                    out.write(' '.join(expanded_line) + '\n')
                    i += 1

            else:
                # Non-macro lines
                out.write(line + '\n')

    print("Macro Pass2 Expansion done. :)")

if __name__ == "__main__":
    macro_pass2()
